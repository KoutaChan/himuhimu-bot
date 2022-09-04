package xyz.n7mn.dev.game.blackjack.data;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import xyz.n7mn.dev.game.blackjack.BlackJack;

@Getter @Setter
public class EmbedPack {
    private EmbedBuilder embedBuilder;
    private BlackJack blackJack;
    private StringBuilder dealer, player;

    public EmbedPack(EmbedBuilder embedBuilder, BlackJack blackJack, StringBuilder dealer, StringBuilder player) {
        this.embedBuilder = embedBuilder;
        this.blackJack = blackJack;
        this.dealer = dealer;
        this.player = player;
    }
}