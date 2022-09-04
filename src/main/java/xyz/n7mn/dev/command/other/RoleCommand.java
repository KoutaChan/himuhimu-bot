package xyz.n7mn.dev.command.other;

import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;

@CommandName(command = {"h.role"}, channelType = ChannelType.TEXT, commandType = CommandType.OTHER, help = "ロール")
public class RoleCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {

    }
}
