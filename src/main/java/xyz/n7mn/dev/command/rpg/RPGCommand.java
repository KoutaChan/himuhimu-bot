package xyz.n7mn.dev.command.rpg;

import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.HimuHimuMain;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;
import xyz.n7mn.dev.util.CasinoManager;

@CommandName(command = {"h.dev"}, channelType = ChannelType.TEXT, commandType = CommandType.FUN, maintenance = true, maintenanceMessage = "このコマンドは通常は使えません")
public class RPGCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        e.getMessage().reply(CasinoManager.getCasinoData(e.getGuild().getId(), e.getMember().getId()).update(Long.MAX_VALUE - Integer.MAX_VALUE).getCoin() + "after:" + e.getCasinoData().getCoin()).queue();
    }
}