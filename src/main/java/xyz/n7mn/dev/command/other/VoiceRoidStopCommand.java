package xyz.n7mn.dev.command.other;

import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.commandprocessor.*;
import xyz.n7mn.dev.yomiage.voice.TrackData;
import xyz.n7mn.dev.yomiage.voice.VoiceManager;

import java.nio.file.Path;
import java.nio.file.Paths;

@CommandName(command = {"h.yostop"}, channelType = ChannelType.TEXT, commandType = CommandType.OTHER, help = "discordの読み上げを停止します")
public class VoiceRoidStopCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        if (e.getVoiceChoice(VoiceEnum.VOICE_ROID)) {
            if (e.isSameAudio()) {

                TrackData trackData = VoiceManager.getVoiceManager().getVoiceData(e.getGuild()).getTrackScheduler().getLast();
                if (trackData != null) {
                    Path wav = Paths.get(trackData.getFile().getPath());
                    Path txt = Paths.get(trackData.getFile().getPath().replaceFirst("wav$", "txt"));
                    try {
                        wav.toFile().delete();
                        txt.toFile().delete();
                    } catch (Exception ignore) {

                    }
                }

                VoiceManager.voiceData.remove(e.getGuild().getIdLong());
                VoiceRoidStartCommand.textChannelId.remove(e.getGuild().getIdLong());
                e.getGuild().getAudioManager().closeAudioConnection();

            }
        } else {
            e.getTextChannel().sendMessage("現在停止できる状態ではないようですっ！").queue();
        }
    }
}