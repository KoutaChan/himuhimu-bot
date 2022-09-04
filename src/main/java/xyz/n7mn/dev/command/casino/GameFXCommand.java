package xyz.n7mn.dev.command.casino;

import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.game.FXMain;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;
import xyz.n7mn.dev.sqlite.data.CasinoData;

@CommandName(command = {"h.fx"}, channelType = ChannelType.TEXT, commandType = CommandType.CASINO, help = "FXモドキ - FX")
public class GameFXCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        if (e.checkCasino(true)) {
            try {
                final long coin = Integer.parseInt(e.getArgs()[1]);

                final long multiply = coin / 10000 + 1;

                final CasinoData casinoData = e.getCasinoData();
                casinoData.update(-coin);

                final FXMain fxMain = new FXMain(coin, multiply).start();

                casinoData.update(fxMain.getRefund());
                e.getMessage().replyEmbeds(fxMain.getEmbedBuilder().build()).queue();
            } catch (Exception ex) {
                e.getMessage().reply("申し訳ありません！ このカジノゲームで可能な最大賭けコインは" + Integer.MAX_VALUE + "です！").queue();
            }
        }
    }
}