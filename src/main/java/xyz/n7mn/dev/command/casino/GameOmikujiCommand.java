package xyz.n7mn.dev.command.casino;

import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.game.Omikuji;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;
import xyz.n7mn.dev.sqlite.data.CasinoData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@CommandName(command = {"h.omikuji"}, channelType = ChannelType.TEXT, commandType = CommandType.CASINO, help = "おみくじ")
public class GameOmikujiCommand extends Command {

    Omikuji[] omikujis = new Omikuji[]{
            new Omikuji("凶", "ざんねーん！", 1),
            new Omikuji("吉", "すごい微妙？", 15),
            new Omikuji("小吉", "ちょっと微妙？", 30),
            new Omikuji("中吉", "ふつうだね！", 50),
            new Omikuji("大吉", "やったね！", 100),
    };

    @Override
    public void CommandEvent(DiscordData e) {
        final List<Omikuji> omikujiList = Arrays.asList(omikujis);
        Collections.shuffle(omikujiList);

        final Omikuji omikuji = omikujiList.get(0);

        final CasinoData casinoData = e.getCasinoData();

        e.getMessage().reply(omikuji.getResultComment() + "\nあなたの運勢は " + omikuji.getResultText() + " でした！\n(獲得コインは" + omikuji.getCoins() + "です。 増加値 " + casinoData.getCoin() + " → " + casinoData.update(omikuji.getCoins()).getCoin() + ")").queue();
    }
}