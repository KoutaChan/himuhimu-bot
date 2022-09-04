package xyz.n7mn.dev.command.special;

import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;

@CommandName(command = {"(⋈◍＞◡＜◍)。✧♡"}, channelType = ChannelType.TEXT, commandType = CommandType.SPECIAL, startWith = true)
public class SpecialByPoti extends Command {
    @Override
    public void CommandEvent(DiscordData e) {
        e.getMessage().reply("(⋈◍＞◡＜◍)。✧♡").queue();
    }
}
