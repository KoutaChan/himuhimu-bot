package xyz.n7mn.dev.game;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import xyz.n7mn.dev.util.MathUtil;

import java.awt.*;
import java.security.SecureRandom;

@Getter @Setter
public class FXMain {
    private long coin, multiply, total, finalCoin, refund;
    private EmbedBuilder embedBuilder = new EmbedBuilder();

    public FXMain(long coin, long multiply) {
        this.coin = coin;
        this.multiply = multiply > 600 ? 600 : multiply;
        this.total = MathUtil.multiply(coin, this.multiply);
        embedBuilder
                .setTitle("FX - " + this.multiply + "倍")
                .setColor(Color.YELLOW)
                .addField("金額", total + "コイン (" + this.multiply + " 倍) でFX開始！", false);
    }

    public FXMain start() {
        return start(5);
    }

    public FXMain start(int count) {
        StringBuilder stringBuilder = new StringBuilder();

        SecureRandom random = new SecureRandom();
        for (int i = 1; i <= count; i++) {
            final long resultCoin = resultCoin(1 + (2.5 * random.nextDouble()));

            if (random.nextBoolean()) {
                stringBuilder.append(i).append("回目: ").append(Math.abs(resultCoin)).append("上がったよ\n");
                this.finalCoin = MathUtil.add(finalCoin, resultCoin, true);
            } else {
                stringBuilder.append(i).append("回目: ").append(Math.abs(resultCoin)).append("下がったよ\n");
                this.finalCoin = MathUtil.subtract(finalCoin, resultCoin, false);
            }
        }

        this.refund = MathUtil.subtract(finalCoin, total, false);

        stringBuilder.append("最終的に").append(finalCoin).append("コインになったよ！\n\n");
        embedBuilder.addField("ゲーム", stringBuilder.toString(), false)
                .addField("獲得コイン", refund + "コイン獲得したよ！", false)
                .addField("単純獲得コイン", MathUtil.subtract(refund, coin, false) + "コイン", false);
        return this;
    }

    public long resultCoin(double multiply) {
        return MathUtil.multiply(total, multiply);
    }
}