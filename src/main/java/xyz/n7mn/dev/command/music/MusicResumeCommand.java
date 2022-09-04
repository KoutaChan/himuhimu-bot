package xyz.n7mn.dev.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.music.MusicManager;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;

@CommandName(command = {"h.resume"}, channelType = ChannelType.TEXT, commandType = CommandType.MUSIC, help = "BGMを再開します")
public class MusicResumeCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        if (e.checkMusic(true)) {
            final AudioPlayer audioPlayer = MusicManager.getMusicManager().getMusicData(e.getGuild()).getAudioPlayer();
            audioPlayer.setPaused(!audioPlayer.isPaused());

            if (audioPlayer.isPaused()) {
                e.getMessage().reply("再生を停止しました！").queue();
            } else {
                e.getMessage().reply("再生を再開しました！").queue();
            }
        }
    }
}