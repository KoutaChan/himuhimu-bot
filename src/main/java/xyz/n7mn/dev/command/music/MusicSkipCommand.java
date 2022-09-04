package xyz.n7mn.dev.command.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.music.AudioTrackData;
import xyz.n7mn.dev.music.MusicManager;
import xyz.n7mn.dev.music.TrackScheduler;
import xyz.n7mn.dev.commandprocessor.*;

import java.awt.*;

@CommandName(command = {"h.skip"}, channelType = ChannelType.TEXT, commandType = CommandType.MUSIC, help = "BGMをスキップします")
public class MusicSkipCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        if (e.checkMusic(true)) {

            MusicManager.getMusicManager().getMusicData(e.getGuild()).getTrackScheduler().nextTrack();

            try {
                final TrackScheduler audioTrackInfo = MusicManager.getMusicManager().getMusicData(e.getGuild()).getTrackScheduler();

                AudioTrackData data = audioTrackInfo.getAudioTrack();

                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("スキップして次の曲を再生します")
                        .setDescription("曲の情報はこちらです！")
                        .setColor(Color.PINK)
                        .addField("タイトル", data.getTitle(), false)
                        .addField("URL", data.getUrl(), false)
                        .addField("再生時間", "```[" + data.getAudioTrack().getPosition() / 1000 + " / " + (data.getAudioTrack().getDuration() == Long.MAX_VALUE ? "実質無限" : data.getAudioTrack().getDuration() / 1000) + "]```", false);

                e.getMessage().replyEmbeds(embedBuilder.build()).queue();
            } catch (NullPointerException ignore) {
                e.getTextChannel().sendMessage("もう再生できる曲はないようだよ！").queue();
            }
        }
    }
}