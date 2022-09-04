package xyz.n7mn.dev.game;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Omikuji {
    private String ResultText;
    private String ResultComment;
    private int Coins;

    public Omikuji(String resultText, String resultComment, int coins){
        this.ResultText = resultText;
        this.ResultComment = resultComment;
        this.Coins = coins;
    }
}
