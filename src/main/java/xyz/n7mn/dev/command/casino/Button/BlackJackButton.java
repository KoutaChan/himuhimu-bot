package xyz.n7mn.dev.command.casino.Button;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import xyz.n7mn.dev.buttonprocessor.Button;
import xyz.n7mn.dev.buttonprocessor.ButtonName;
import xyz.n7mn.dev.game.blackjack.BlackJack;
import xyz.n7mn.dev.game.blackjack.data.BlackJackData;
import xyz.n7mn.dev.game.blackjack.data.EmbedPack;
import xyz.n7mn.dev.sqlite.data.CasinoData;
import xyz.n7mn.dev.util.CasinoManager;
import xyz.n7mn.dev.util.MathUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ButtonName(componentId = {"blackjack-card", "blackjack-end", "blackjack-double"})
public class BlackJackButton extends Button {

    /**
     * ここら辺のコードは難読化されてるレベルで見にくいです
     */

    @Override
    public void ButtonEvent(ButtonInteractionEvent e) {

        MessageEmbed messageEmbed = e.getMessage().getEmbeds().get(0);
        if (messageEmbed.getFooter().getText().equals(e.getMember().getId())) {

            EmbedPack embedPack = blackJack(e.getMember(), messageEmbed);
            CasinoData casinoData = CasinoManager.getCasinoData(e.getGuild().getId(), e.getUser().getId());
            switch (e.getComponentId()) {
                case "blackjack-card": {
                    BlackJackData data = embedPack.getBlackJack().getRandomBJ(false);

                    embedPack.getPlayer().append("\n").append(data.message());

                    if (embedPack.getBlackJack().getPlayer() > 21) {

                        EmbedPack dealerEmbedPack = startDealer(embedPack);

                        embedPack(dealerEmbedPack, casinoData);

                        e.editMessageEmbeds(dealerEmbedPack.getEmbedBuilder().build()).setActionRow(
                                e.getMessage().getButtonById("blackjack-card").asDisabled(),
                                e.getMessage().getButtonById("blackjack-end").asDisabled(),
                                e.getMessage().getButtonById("blackjack-double").asDisabled()
                        ).queue();
                    } else {
                        embedPack.getEmbedBuilder().addField("掛け金", embedPack.getBlackJack().getCoin() + "コイン", false);
                        embedPack.getEmbedBuilder().addField("\uD83C\uDCCF ディーラー" + " (" + embedPack.getBlackJack().getDealer() + ")", String.valueOf(embedPack.getDealer()), false);

                        embedPack.getEmbedBuilder().addField("\uD83C\uDCCF プレイヤー" + " (" + embedPack.getBlackJack().getPlayer() + ")", String.valueOf(embedPack.getPlayer()), false);
                        e.editMessageEmbeds(embedPack.getEmbedBuilder().build()).setActionRow(
                                e.getMessage().getButtonById("blackjack-card"),
                                e.getMessage().getButtonById("blackjack-end"),
                                e.getMessage().getButtonById("blackjack-double").asDisabled()
                        ).queue();
                    }
                    break;
                }
                case "blackjack-end": {

                    EmbedPack dealerEmbedPack = startDealer(embedPack);
                    embedPack(dealerEmbedPack, casinoData);

                    e.editMessageEmbeds(dealerEmbedPack.getEmbedBuilder().build()).setActionRow(
                            e.getMessage().getButtonById("blackjack-card").asDisabled(),
                            e.getMessage().getButtonById("blackjack-end").asDisabled(),
                            e.getMessage().getButtonById("blackjack-double").asDisabled()
                    ).queue();

                    break;
                }
                case "blackjack-double": {
                    if (casinoData.hasCoin(embedPack.getBlackJack().getCoin())) {
                        embedPack.getEmbedBuilder().addField("ダブルダウン", embedPack.getBlackJack().getCoin() + "コインを使いました", false);
                    } else {
                        embedPack.getEmbedBuilder().addField("借金", (embedPack.getBlackJack().getCoin() - casinoData.getCoin()) + "コイン借金しました", false);
                    }

                    casinoData.update(-embedPack.getBlackJack().getCoin());
                    embedPack.getBlackJack().setCoin(MathUtil.multiply(embedPack.getBlackJack().getCoin(), 2d));

                    BlackJackData data = embedPack.getBlackJack().getRandomBJ(false);
                    embedPack.getPlayer().append("\n").append(data.message());

                    EmbedPack dealerEmbedPack = startDealer(embedPack);
                    embedPack(dealerEmbedPack, casinoData);

                    e.editMessageEmbeds(dealerEmbedPack.getEmbedBuilder().build()).setActionRow(
                            e.getMessage().getButtonById("blackjack-card").asDisabled(),
                            e.getMessage().getButtonById("blackjack-end").asDisabled(),
                            e.getMessage().getButtonById("blackjack-double").asDisabled()
                    ).queue();

                    break;
                }
            }
        }
    }

    public void embedPack(EmbedPack embedPack, CasinoData casinoData) {
        switch (embedPack.getBlackJack().resultCheck()) {
            case DEALER_BLACKJACK:
                embedPack.getEmbedBuilder()
                        .setDescription("あなたの負け！ ディーラーはブラックジャックでした！")
                        .setColor(Color.RED)
                        .addField("コイン増加値", MathUtil.add(casinoData.getCoin(), embedPack.getBlackJack().getCoin(), true) + " → " + casinoData.update(embedPack.getBlackJack().resultCoin(0)).getCoin(), false);
                break;
            case PLAYER_BLACKJACK:
                //ここには多分到達しない
                embedPack.getEmbedBuilder()
                        .setDescription("あなたの勝ち！ あなたはブラックジャックでした！ラッキー！")
                        .setColor(Color.CYAN)
                        .addField("コイン増加値", MathUtil.add(casinoData.getCoin(), embedPack.getBlackJack().getCoin(), true) + " → " + casinoData.update(embedPack.getBlackJack().resultCoin(2.5)).getCoin(), false);
                break;
            case DRAW:
                embedPack.getEmbedBuilder()
                        .setDescription("引き分けのため、返金されます！")
                        .setColor(Color.PINK)
                        .addField("コイン増加値", MathUtil.add(casinoData.getCoin(), embedPack.getBlackJack().getCoin(), true) + " → " + casinoData.update(embedPack.getBlackJack().getCoin()).getCoin(), false);
                break;
            case PLAYER:
                embedPack.getEmbedBuilder()
                        .setDescription("あなたの勝ち！")
                        .setColor(Color.YELLOW)
                        .addField("コイン増加値", MathUtil.add(casinoData.getCoin(), embedPack.getBlackJack().getCoin(), true) + " → " + casinoData.update(embedPack.getBlackJack().resultCoin(2)).getCoin(), false);
                break;
            case DEALER:
                embedPack.getEmbedBuilder()
                        .setDescription("あなたの負け！")
                        .setColor(Color.RED)
                        .addField("コイン増加値", MathUtil.add(casinoData.getCoin(), embedPack.getBlackJack().getCoin(), true) + " → " + casinoData.update(embedPack.getBlackJack().resultCoin(0)).getCoin(), false);
                break;
        }
    }

    public EmbedPack startDealer(EmbedPack embedPack) {

        StringBuilder stringBuilder = new StringBuilder();

        while (17 > embedPack.getBlackJack().getDealer()) {
            BlackJackData data = embedPack.getBlackJack().getRandomBJ(true);
            stringBuilder.append("\n").append(data.message());
            if (embedPack.getBlackJack().getDealerList().size() == 2 && embedPack.getBlackJack().isBlackJack(true)) {
                break;
            }
        }

        embedPack.getEmbedBuilder().addField("掛け金", embedPack.getBlackJack().getCoin() + "コイン", false);

        embedPack.getEmbedBuilder().addField("\uD83C\uDCCF ディーラー" + " (" + embedPack.getBlackJack().getDealer() + ")", String.valueOf(embedPack.getDealer().append(stringBuilder)), false);

        embedPack.getEmbedBuilder().addField("\uD83C\uDCCF プレイヤー" + " (" + embedPack.getBlackJack().getPlayer() + ")", String.valueOf(embedPack.getPlayer()), false);

        return embedPack;
    }


    public EmbedPack blackJack(Member member, MessageEmbed messageEmbed) {

        List<String> dealerCard = new ArrayList<>();
        List<String> playerCard = new ArrayList<>();

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("ブラックジャック - 21になるように調整しろ！")
                .setColor(Color.YELLOW)
                .addField("ユーザー", member.getEffectiveName() + " (" + member.getId() + ")", false)
                .setFooter(member.getId());

        long coin = 0;
        StringBuilder dealer = new StringBuilder();
        StringBuilder player = new StringBuilder();

        for (MessageEmbed.Field field : messageEmbed.getFields()) {

            if (field.getName().equals("掛け金")) {
                coin = Long.parseLong(field.getValue().replaceAll("[^\\d]", ""));
            }

            if (field.getName().contains("ディーラー") || field.getName().contains("プレイヤー")) {
                String[] split = field.getValue().split("\n");

                for (String str : split) {

                    String[] args = str.split(" ");

                    if (field.getName().contains("ディーラー")) {
                        dealer.append("\n").append(str);
                        dealerCard.add(args[0]);
                    } else {
                        player.append("\n").append(str);
                        playerCard.add(args[0]);
                    }
                }
            }
        }

        return new EmbedPack(embedBuilder, new BlackJack(coin, dealerCard, playerCard), dealer, player);
    }
}