package xyz.n7mn.dev.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.music.AudioTrackData;
import xyz.n7mn.dev.music.MusicData;
import xyz.n7mn.dev.music.MusicManager;
import xyz.n7mn.dev.commandprocessor.*;

import java.awt.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CommandName(command = {"h.nowplay"}, channelType = ChannelType.TEXT, commandType = CommandType.MUSIC, help = "現在再生されているBGMを表示します")
public class MusicNowPlayCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        if (e.checkMusic(true)) {
            final MusicData musicData = MusicManager.getMusicManager().getMusicData(e.getGuild());

            final AudioTrack track = musicData.getAudioPlayer().getPlayingTrack();
            if (track != null && track.getInfo().title != null) {
                AudioTrackData audioTrack = musicData.getTrackScheduler().getAudioTrack();

                // https://github.com/Mw3y/Text-ProgressBar/blob/master/ProgressBar.js
                final double percentage = ((double) track.getPosition() / 1000) / ((double) track.getDuration() / 1000);
                final long progress = Math.round(30 * percentage);
                final long emptyProgress = 30 - progress;

                final String progressText = Stream.generate(() -> "▇").limit(progress).collect(Collectors.joining());
                final String emptyProgressText = Stream.generate(() -> "—").limit(emptyProgress).collect(Collectors.joining());

                final EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setColor(Color.PINK)
                        .setTitle(audioTrack.getTitle())
                        .setDescription("現在再生中の曲の情報はこちらです！")
                        .addField("音量", "```" + musicData.getAudioPlayer().getVolume() + "%```", false)
                        .addField("リピート？", "```" + (musicData.getTrackScheduler().isRepeated() ? "はい" : "いいえ") + "```", false)
                        .addField("プレイ時間", "```[" + track.getPosition() / 1000 + " / " + (track.getDuration() == Long.MAX_VALUE ? "実質無限" : track.getDuration() / 1000) + "]```", false)
                        .addField("プレイ時間(プログレスバー)", "```[" + progressText + emptyProgressText + "]" + Math.round(percentage * 100) + "%```", false);
                e.getTextChannel().sendMessageEmbeds(embedBuilder.build()).queue();
            } else {
                e.getTextChannel().sendMessage("もう再生されてる曲はないそうですよっ！").queue();
            }
        }
    }
}