package xyz.n7mn.dev.voice.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.n7mn.dev.HimuHimuMain;
import xyz.n7mn.dev.util.TimeUtils;
import xyz.n7mn.dev.voice.AudioListener;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;
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

    public MusicScheduler(AudioPlayerManager manager, AudioPlayer player) {
        super(player);
        this.manager = manager;
    }

    public void queue(AudioTrackData data) {
        boolean started = player.startTrack(track, true);

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

    public static void main(String[] args) {

    }

    public void search(TextChannel textChannel, String url) {
        if (URL_PATTERN.matcher(url).matches()) {

        } else {

        }
    }

    private String YOUTUBE_API = HimuHimuMain.configManager.getString("youtube.url") +"?text=";

    public void searchLegacy(TextChannel textChannel, String name) {
        String url = YOUTUBE_API + name;

        if (URL_PATTERN.matcher(url).matches()) {
            Request request = new Request.Builder().url(url).build();

            new OkHttpClient().newBuilder().callTimeout(5, TimeUnit.SECONDS)
                    .build()
                    .newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            textChannel.sendMessage("[API] APIでエラーが発生しました。 もう一回試してみてください").queue();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                try (ResponseBody body = Objects.requireNonNull(response.body())) {
                                    JSONArray context = new JSONObject(body.string()).getJSONArray("items");

                                    EmbedBuilder embed = new EmbedBuilder();

                                    if (context.length() == 0) {
                                        embed.setTitle("検索候補が見つかりませんでした！")
                                                .setDescription(name + "で検索しました");
                                                //.setTimestamp();
                                        /* ZERO MESSAGE */
                                    } else {
                                        for (int i = 0; i < context.length(); i++) {
                                            JSONObject videoInfo = context.getJSONObject(i);

                                        }
                                    }
                                }
                            }
                        }
                    });
        }
    }
    /*
        public void searchYoutube(String trackURL, TextChannel channel) {
        String url = HimuHimuMain.configManager.getString("youtube.url") + "?text=" + trackURL;

        if (!url.equals("URL_HERE")) {

            //JsoupだとJsonが勝手に置き換えられてました
            Request request = new Request.Builder().url(url)
                    .build();

            new OkHttpClient().newBuilder().callTimeout(5, TimeUnit.SECONDS)
                    .build()
                    .newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            try (ResponseBody body = response.body()) {

                                if (!response.isSuccessful()) {
                                    System.out.print("print the error:\n");
                                    System.out.print(body.string() + "\n");

                                    throw new IOException("timeout ?" + response);
                                }

                                JSONObject jsonObject = new JSONObject(body.string());
                                JSONArray videoArray = jsonObject.getJSONArray("items");

                                EmbedBuilder embedBuilder = new EmbedBuilder()
                                        .setFooter("YouTube API v3. - ベータ版 API上限になる可能性があります");

                                List<ItemComponent> itemComponentList = new ArrayList<>();

                                for (int i = 0; i < videoArray.length(); i++) {
                                    JSONObject videInfo = videoArray.getJSONObject(i);

                                    final int count = i + 1;

                                    JSONObject id = videInfo.getJSONObject("id");
                                    JSONObject snippet = videInfo.getJSONObject("snippet");

                                    embedBuilder.addField("[" + count + "] " + HtmlUtils.getString(snippet.getString("title")), "https://www.youtube.com/watch?v=" + id.getString("videoId"), false);

                                    itemComponentList.add(Button.secondary("music-" + count, Emoji.fromUnicode("U+003" + count + " U+FE0F U+20E3")));
                                }

                                if (itemComponentList.size() == 0) {
                                    embedBuilder.setTitle("Not Found!");

                                    channel.sendMessageEmbeds(embedBuilder.build()).queue();
                                } else {
                                    embedBuilder.setTitle("再生可能リスト (" + itemComponentList.size() + ") 個の中から選択してください)");

                                    channel.sendMessageEmbeds(embedBuilder.build()).setActionRow(itemComponentList).queue();
                                }
                            }
                        }
                    });
        }
    }
}
     */

    public void searchAndPlay(TextChannel textChannel, String url) {
        manager.loadItem(url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                queue(track);

                if (textChannel != null) {
                    textChannel.sendMessageEmbeds(new EmbedBuilder()
                            .setTitle(">> 再生する音楽を追加しました")
                            .setColor(Color.PINK)
                            .addField("[~] タイトル", track.getInfo().title, false)
                            .addField("[!] URL", track.getInfo().title, false)
                            .addField("[+] 再生時間", track.getInfo().isStream ? "[!] ライブ配信" : TimeUtils.convertSecondToFormat(track.getDuration()), false)
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
                    textChannel.sendMessageEmbeds(new EmbedBuilder()
                                    .setTitle(">> 再生する音楽を追加しました")
                                    .setColor(Color.PINK)
                                    .addField("[~] タイトル", track.getInfo().title, false)
                                    .addField("[+] 再生時間", TimeUtils.convertSecondToFormat(total), false)
                                    .build())
                            .queue();
                }
            }

            @Override
            public void noMatches() {
                if (textChannel != null) {
                    textChannel.sendMessageEmbeds(new EmbedBuilder()
                                    .setTitle(">> エラー！")
                                    .setDescription("[!] 再生できないコンテンツです")
                                    .setColor(Color.RED)
                                    .build())
                            .queue();
                }
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                if (textChannel != null) {
                    textChannel.sendMessageEmbeds(new EmbedBuilder()
                            .setTitle(">> エラー！")
                            .setDescription("[!] 再生できないURLです！")
                            .setColor(Color.RED)
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
        if (endReason.mayStartNext) {

        } else {

        }
         super.onTrackEnd(player, track, endReason);
    }

    @Override
    public void exit() {
        track.stop();
    }
}