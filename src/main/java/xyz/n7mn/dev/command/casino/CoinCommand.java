package xyz.n7mn.dev.command.casino;

import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;
import xyz.n7mn.dev.sqlite.data.CasinoData;
import xyz.n7mn.dev.util.CasinoManager;

@CommandName(command = {"h.coin", "h.money"}, channelType = ChannelType.TEXT, commandType = CommandType.CASINO, help = "現在あなたが持っているコインを確認します")
public class CoinCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        final CasinoData casinoData = CasinoManager.getCasinoData(e.getGuild().getId(), e.getMember().getId());

        e.getMessage().reply("あなたのコインは「 " + casinoData.getCoin() + "コイン 」です").queue();
    }
}
