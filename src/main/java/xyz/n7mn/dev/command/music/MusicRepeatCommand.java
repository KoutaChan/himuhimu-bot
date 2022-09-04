package xyz.n7mn.dev.command.music;

import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.music.MusicManager;
import xyz.n7mn.dev.music.TrackScheduler;
import xyz.n7mn.dev.commandprocessor.*;

@CommandName(command = {"h.repeat", "h.loop"}, channelType = ChannelType.TEXT, commandType = CommandType.MUSIC, help = "BGMをリピートします")
public class MusicRepeatCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        if (e.checkMusic(true)) {

            final TrackScheduler trackScheduler = MusicManager.getMusicManager().getMusicData(e.getGuild()).getTrackScheduler();

            final boolean isRepeated = !trackScheduler.isRepeated();

            trackScheduler.setRepeated(isRepeated);

            e.getTextChannel().sendMessage("リピートモードを" + ((isRepeated) ? "ONにしました" : "オフにしました！")).queue();
        }
    }
}