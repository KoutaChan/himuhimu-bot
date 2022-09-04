package xyz.n7mn.dev.command.casino;

import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;
import xyz.n7mn.dev.sqlite.data.CasinoData;

@CommandName(command = {"h.kyuusai"}, channelType = ChannelType.TEXT, commandType = CommandType.CASINO, help = "コインがマイナスになったときにコインを0にしてくれます")
public class KyuusaiCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {

        final CasinoData casinoData = e.getCasinoData();

        if (casinoData.getCoin() < 0) {
            casinoData.set(0);
            e.getMessage().reply("[救済措置] コインを0に設定しました").queue();
        }
    }
}