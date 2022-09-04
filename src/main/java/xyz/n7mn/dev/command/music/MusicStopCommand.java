package xyz.n7mn.dev.command.music;

import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.music.AudioTrackData;
import xyz.n7mn.dev.music.MusicManager;
import xyz.n7mn.dev.commandprocessor.*;

@CommandName(command = {"h.stop"}, channelType = ChannelType.TEXT, commandType = CommandType.MUSIC, help = "BGMを強制終了させます")
public class MusicStopCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        if (e.checkMusic(true)) {

            final AudioTrackData audioTrack = MusicManager.getMusicManager().getMusicData(e.getGuild()).getTrackScheduler().getAudioTrack();

            if (audioTrack != null && audioTrack.getNicoVideo() != null && audioTrack.getNicoVideo().isSuccess()) {
                audioTrack.getNicoVideo().setBreak(true);
            }

            MusicManager.getMusicManager().getMusicData(e.getGuild()).getAudioPlayer().destroy();

            MusicManager.musicData.remove(e.getGuild().getIdLong());
            e.getGuild().getAudioManager().closeAudioConnection();
        }
    }
}