package xyz.n7mn.dev.command.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.music.AudioTrackData;
import xyz.n7mn.dev.music.MusicData;
import xyz.n7mn.dev.music.MusicManager;
import xyz.n7mn.dev.commandprocessor.*;

import java.awt.*;

@CommandName(command = {"h.playlist"}, channelType = ChannelType.TEXT, commandType = CommandType.MUSIC, help = "現在キューに入ってるミュージックを表示します")
public class MusicPlayListCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        if (e.checkMusic(true)) {
            final MusicData musicData = MusicManager.getMusicManager().getMusicData(e.getGuild());
            final EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("再生リスト一覧！")
                    .setDescription("最大10個までしか表示されていません！")
                    .setColor(Color.PINK);

            final int length = embedBuilder.length();

            if (musicData.getAudioPlayer().getPlayingTrack() != null) {
                doIt(embedBuilder, musicData.getTrackScheduler().getAudioTrack());

                musicData.getTrackScheduler().getQueue().stream().limit(9).forEach(data -> doIt(embedBuilder, data));

                if (embedBuilder.length() == length || embedBuilder.isEmpty()) {
                    e.getTextChannel().sendMessage("キューに入ってる曲はないようだよっ！").queue();
                } else {
                    e.getTextChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                }
            } else {
                e.getTextChannel().sendMessage("キューに入ってる曲はないようだよっ！").queue();
            }
        }
    }

    private void doIt(EmbedBuilder embedBuilder, AudioTrackData data) {
        if (data == null || data.getAudioTrack() == null) return;

        String name = data.getTitle();
        final long position = data.getAudioTrack().getPosition();
        final long duration = data.getAudioTrack().getDuration();
        if (name.length() > 25) name = name.substring(0, 22) + "...";

        embedBuilder.addField(name, "```[" + position / 1000 + " / " + (duration == Long.MAX_VALUE ? "実質無限" : duration / 1000) + "]```", false);
    }
}