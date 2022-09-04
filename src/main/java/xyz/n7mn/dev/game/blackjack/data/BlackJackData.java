package xyz.n7mn.dev.game.blackjack.data;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BlackJackData {
    private String cardString;
    private Integer number;
    private boolean used = false;

    public BlackJackData(String cardString, Integer number) {
        this.cardString = cardString;
        this.number = number;
    }

    public String translate() {
        return cardString.replaceAll(":spades:", "スペード")
                .replaceAll(":clubs:", "クラブ")
                .replaceAll(":hearts:", "ハート")
                .replaceAll(":diamonds:", "ダイヤ");
    }

    public String message() {
        return cardString + number + " (" + translate() + "の" + number + ")";
    }
}