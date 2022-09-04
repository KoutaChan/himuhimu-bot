package xyz.n7mn.dev.command.casino;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import xyz.n7mn.dev.game.blackjack.BlackJack;
import xyz.n7mn.dev.game.blackjack.data.BlackJackData;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;
import xyz.n7mn.dev.sqlite.data.CasinoData;
import xyz.n7mn.dev.util.MathUtil;

import java.awt.*;

@CommandName(command = {"h.blackjack", "h.bj"}, channelType = ChannelType.TEXT, commandType = CommandType.CASINO, help = "21を狙え！ - ブラックジャック")
public class GameBlackJackCommand extends Command {

    /**
     * ここら辺のコードは難読化されてるレベルで見にくいです
     */

    @Override
    public void CommandEvent(DiscordData e) {
        if (e.checkCasino(true)) {
            long coin = Long.parseLong(e.getArgs()[1]);
            CasinoData casinoData = e.getCasinoData()
                    .update(-coin);

            BlackJack blackJack = new BlackJack(coin);
            BlackJackData dealer1 = blackJack.getRandomBJ(true);
            BlackJackData player1 = blackJack.getRandomBJ(false);
            BlackJackData player2 = blackJack.getRandomBJ(false);

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("ブラックジャック - 21になるように調整しろ！")
                    .setColor(Color.YELLOW)
                    .addField("ユーザー", e.getMember().getEffectiveName() + " (" + e.getMember().getId() + ")", false)
                    .addField("掛け金", coin + "コイン", false)
                    .setFooter(e.getMember().getId());

            boolean isBlackJack = BlackJack.isBlackJack(player1, player2);
            if (isBlackJack) {

                BlackJackData dealer2 = blackJack.getRandomBJ(true);
                if (!BlackJack.isBlackJack(dealer1, dealer2)) {
                    embedBuilder.setDescription("ブラックジャックのため、あなたの勝利です！ラッキー！")
                            .addField("\uD83C\uDCCF ディーラー" + " (" + blackJack.getDealer() + ")", dealer1.message() + "\n" + dealer2.message(), false)
                            .addField("\uD83C\uDCCF プレイヤー" + " (" + blackJack.getPlayer() + ")", player1.message() + "\n" + player2.message(), false)
                            .addField("コイン増加値", MathUtil.add(casinoData.getCoin(), blackJack.getCoin(), true) + " → " + casinoData.update(blackJack.resultCoin(2.5)).getCoin(), false);
                    e.getMessage().replyEmbeds(embedBuilder.build()).setActionRow(
                            Button.primary("blackjack-card", "カードを引く").asDisabled(),
                            Button.primary("blackjack-end", "ターンを終わる").asDisabled(),
                            Button.primary("blackjack-double", "ダブルダウン").asDisabled()
                    ).queue();
                } else {
                    embedBuilder.setDescription("引き分けのため、返金されます")
                            .addField("\uD83C\uDCCF ディーラー" + " (" + blackJack.getDealer() + ")", dealer1.message() + "\n" + dealer2.message(), false)
                            .addField("\uD83C\uDCCF プレイヤー" + " (" + blackJack.getPlayer() + ")", player1.message() + "\n" + player2.message(), false)
                            .addField("コイン増加値", MathUtil.add(casinoData.getCoin(), blackJack.getCoin(), true)+ " → " + casinoData.update(blackJack.resultCoin(1)).getCoin(), false);
                    e.getMessage().replyEmbeds(embedBuilder.build()).setActionRow(
                            Button.primary("blackjack-card", "カードを引く").asDisabled(),
                            Button.primary("blackjack-end", "ターンを終わる").asDisabled(),
                            Button.primary("blackjack-double", "ダブルダウン").asDisabled()
                    ).queue();
                }
            } else {
                embedBuilder.addField("\uD83C\uDCCF ディーラー" + " (" + blackJack.getDealer() + ")", dealer1.message(), false)
                        .addField("\uD83C\uDCCF プレイヤー" + " (" + blackJack.getPlayer() + ")", player1.message() + "\n" + player2.message(), false);
                e.getMessage().replyEmbeds(embedBuilder.build()).setActionRow(
                        Button.primary("blackjack-card", "カードを引く"),
                        Button.primary("blackjack-end", "ターンを終わる"),
                        Button.primary("blackjack-double", "ダブルダウン")
                ).queue();
            }
        }
    }
}