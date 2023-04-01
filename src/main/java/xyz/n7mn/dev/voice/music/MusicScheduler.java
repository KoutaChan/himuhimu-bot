package xyz.n7mn.dev.voice.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.n7mn.dev.HimuHimuMain;
import xyz.n7mn.dev.util.DiscordUtil;
import xyz.n7mn.dev.util.HtmlUtils;
import xyz.n7mn.dev.util.TimeUtils;
import xyz.n7mn.dev.voice.AudioListener;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * {@link com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager}
 */
public class MusicScheduler extends AudioListener {
    private final BlockingQueue<AudioTrackData> queue = new LinkedBlockingQueue<>();
    private AudioPlayerManager manager;
    private boolean repeat;

    public MusicScheduler(AudioPlayerManager manager, AudioPlayer player) {
        super(player);
        this.manager = manager;
    }

    public void queue(AudioTrackData data) {
        boolean started = player.startTrack(data.track(), true);

        if (!started) {
            this.track = data.getTrack();
        } else {
            queue.add(data);
        }
    }

    public void queue(MusicType type, AudioTrack track, Consumer<AudioTrack> onTracking) {
        queue(new AudioTrackData(type, track, onTracking));
    }

    @Override
    public void queue(AudioTrack track) {
        queue(new AudioTrackData(MusicType.LAVA_PLAYER, track, null));
    }

    @Override
    public void load(AudioTrack track) {
        player.startTrack(track, true);
    }

    private final static Pattern URL_PATTERN = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    public void search(IReplyCallback textChannel, String url) {
        if (URL_PATTERN.matcher(url).matches()) {
            searchAndPlay(textChannel, url);
        } else {
            searchOnYoutube(textChannel, url);
        }
    }

    private final String YOUTUBE_API = HimuHimuMain.configManager.getString("youtube.url") + "?text=";
    public void searchOnYoutube(IReplyCallback textChannel, String name) {
        if (!HimuHimuMain.configManager.getBoolean("youtube.search")) {
            return;
        }
        new OkHttpClient().newBuilder().callTimeout(5, TimeUnit.SECONDS)
                .build()
                .newCall(new Request.Builder().url(YOUTUBE_API + name).build())
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        textChannel.getHook().editOriginalEmbeds(DiscordUtil.getErrorEmbeds("[API] APIでエラーが発生しました。 もう一度やり直してください！").build()).queue();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try (ResponseBody body = response.body()) {
                            if (!response.isSuccessful()) {
                                textChannel.getHook().editOriginalEmbeds(DiscordUtil.getErrorEmbeds("[API] HTTPリクエストに失敗しました").build()).queue();
                            } else {
                                JSONArray videoArray = new JSONObject(body.string()).getJSONArray("items");

                                EmbedBuilder content = new EmbedBuilder()
                                        .setTitle(name + "で検索しました (" + videoArray.length() + "件検索しました)")
                                        .setFooter("YouTube API v3. - ベータ版 API上限になる可能性があります");

                                List<ItemComponent> components = new ArrayList<>();
                                for (int i = 0; i < videoArray.length(); i++) {
                                    JSONObject videoInfo = videoArray.getJSONObject(i);
                                    JSONObject id = videoInfo.getJSONObject("id");
                                    JSONObject snippet = videoInfo.getJSONObject("snippet");
                                    content.addField("[" + (i + 1) + "]" + HtmlUtils.getString(snippet.getString("title")), "https://www.youtube.com/watch?v=" + id.getString("videoId"), false);
                                    components.add(Button.secondary("music-id-" + (i + 1), Emoji.fromUnicode("U+003" + (i + 1) + " U+FE0F U+20E3")));
                                }
                                if (components.isEmpty()) {
                                    content.addField("何も見つかりませんでした", "何を検索したんですか？", false);
                                }
                                textChannel.getHook().editOriginalEmbeds(content.build()).setActionRow(components).queue();
                            }
                        }
                    }
                });
    }

    public void searchAndPlay(IReplyCallback textChannel, String url) {
        manager.loadItem(url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                queue(track);

                if (textChannel != null) {
                    textChannel.getHook().editOriginalEmbeds(new EmbedBuilder()
                                    .setTitle(">> 再生する音楽を追加しました")
                                    .setColor(Color.PINK)
                                    .addField("[~] タイトル", track.getInfo().title, false)
                                    .addField("[!] URL", track.getInfo().uri, false)
                                    .addField("[+] 再生時間", track.getInfo().isStream ? "[!] ライブ配信" : TimeUtils.convertMillisecondsToFormat(track.getDuration()), false)
                                    .build())
                            .queue();
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                long total = 0;

                for (AudioTrack track : playlist.getTracks()) {
                    if (!track.getInfo().isStream) {
                        total += track.getDuration();
                    }
                    queue(track);
                }

                if (textChannel != null) {
                    textChannel.getHook().editOriginalEmbeds(new EmbedBuilder()
                                    .setColor(Color.PINK)
                                    .setTitle(">> 再生する音楽を追加しました")
                                    .addField("[~] タイトル", track.getInfo().title, false)
                                    .addField("[!] URL", track.getInfo().uri, false)
                                    .addField("[+] 再生時間", TimeUtils.convertMillisecondsToFormat(total), false)
                                    .build())
                            .queue();
                }
            }

            @Override
            public void noMatches() {
                if (textChannel != null) {
                    textChannel.getHook().editOriginalEmbeds(new EmbedBuilder()
                                    .setColor(Color.RED)
                                    .setTitle(">> エラー！")
                                    .setDescription("[!] 再生できないコンテンツです")
                                    .build())
                            .queue();
                }
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                if (textChannel != null) {
                    textChannel.getHook().editOriginalEmbeds(new EmbedBuilder()
                                    .setColor(Color.RED)
                                    .setTitle(">> エラー！")
                                    .setDescription("[!] 再生できないURLです！")
                                    .addField("エラー内容", exception.getMessage(), false)
                                    .build())
                            .queue();
                }
            }
        });
    }

    @Override
    public void searchAndPlay(String url) {
        searchAndPlay(null, url);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (this.repeat) {
            player.playTrack(track.makeClone());
        } else if (endReason.mayStartNext) {
            player.playTrack(track);
        } else {

        }
        super.onTrackEnd(player, track, endReason);
    }

    public boolean isRepeat() {
        return repeat;
    }

    public boolean setRepeat(boolean newValue) {
        return this.repeat = newValue;
    }

    @Override
    public void exit() {
        track.stop();
    }
}