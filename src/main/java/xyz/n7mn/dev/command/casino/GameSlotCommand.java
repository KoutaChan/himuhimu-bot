package xyz.n7mn.dev.command.casino;

import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.game.slot.SlotMain;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;
import xyz.n7mn.dev.sqlite.data.CasinoData;

@CommandName(command = {"h.slot"}, channelType = ChannelType.TEXT, commandType = CommandType.CASINO, help = "スロットマシーン")
public class GameSlotCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        if (e.checkCasino(true)) {

            final long coin = Long.parseLong(e.getArgs()[1]);

            final CasinoData casinoData = e.getCasinoData()
                    .update(-coin);

            final SlotMain slotMain = new SlotMain(coin);

            if (slotMain.isMatched()) {
                casinoData.update(slotMain.resultCoin());
            }

            e.getMessage().replyEmbeds(slotMain.getEmbedBuilder().build()).queue();
        }
    }
}