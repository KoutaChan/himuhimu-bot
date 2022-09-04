package xyz.n7mn.dev.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.n7mn.dev.HimuHimuMain;
import xyz.n7mn.dev.sqlite.data.SQLiteMusicData;
import xyz.n7mn.dev.util.HtmlUtils;
import xyz.n7mn.dev.util.MusicUtil;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MusicManager {
    public static final AudioPlayerManager manager = new DefaultAudioPlayerManager();
    @Getter
    private static final MusicManager musicManager = new MusicManager();
    public static Map<Long, MusicData> musicData = new HashMap<>();

    public MusicManager() {
        AudioSourceManagers.registerLocalSource(manager);
        AudioSourceManagers.registerRemoteSources(manager);
    }

    public synchronized MusicData getMusicData(Guild guild) {
        if (!musicData.containsKey(guild.getIdLong())) createMusicData(guild);
        return musicData.get(guild.getIdLong());
    }

    public synchronized boolean hasMusicData(Guild guild) {
        return musicData.containsKey(guild.getIdLong());
    }

    public synchronized void createMusicData(Guild guild) {

        MusicData musicData = new MusicData(manager.createPlayer(), guild);

        SQLiteMusicData sqLiteMusicData = MusicUtil.getMusicData(guild);

        final int volume = Math.min(sqLiteMusicData.getDefaultVolume(), sqLiteMusicData.getMaxVolume());

        musicData.getAudioPlayer().setVolume(volume);

        MusicManager.musicData.put(guild.getIdLong(), musicData);
    }

    public void loadAndPlay(TextChannel channel, AudioTrackData data, boolean searchYoutube) {
        MusicData musicManager = getMusicData(channel.getGuild());

        channel.getGuild().getAudioManager().setSendingHandler(musicManager.getAudioHandler());

        manager.loadItemOrdered(musicManager, data.getUrl(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                data.setAudioTrack(track);

                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("再生する音楽を追加しました")
                        .setColor(Color.PINK)
                        .addField("タイトル", data.getTitle(), false)
                        .addField("URL", data.getTargetURL(), false)
                        .addField("再生時間", "```[" + track.getPosition() / 1000 + " / " + (track.getDuration() == Long.MAX_VALUE ? "実質無限" : track.getDuration() / 1000) + "]```", false);

                channel.sendMessageEmbeds(embedBuilder.build()).queue();

                musicManager.playTrack(data);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                channel.sendMessageEmbeds(new EmbedBuilder()
                        .setTitle("再生リストを追加したよ")
                        .addField("プレイリスト名", playlist.getName(), false)
                        .build()).queue();

                for (AudioTrack track : playlist.getTracks()) {
                    musicManager.playTrack(new AudioTrackData(track, null));
                }
            }

            @Override
            public void noMatches() {
                //見つからなかった

                // new api comings
                if (searchYoutube) {
                    searchYoutube(data.getUrl(), channel);
                } else {
                    channel.sendMessage("再生する音楽が見つからなかったよっ").queue();
                }
            }

            @Override
            public void loadFailed(FriendlyException e) {
                //ロード時にエラー発生
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle("エラーが発生しました！")
                        .addField("エラー内容", e.getMessage(), false);


                channel.sendMessageEmbeds(embedBuilder.build()).queue();
            }
        });
    }

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