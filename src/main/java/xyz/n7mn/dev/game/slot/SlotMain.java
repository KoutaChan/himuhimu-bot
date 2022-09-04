package xyz.n7mn.dev.game.slot;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import xyz.n7mn.dev.util.MathUtil;

import java.awt.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter @Setter
public class SlotMain {
    private static List<SlotData> slotDataList = new ArrayList<>();

    static {
        Collections.addAll(
                slotDataList,
                new SlotData(":one:", 1),
                new SlotData(":two:", 2),
                new SlotData(":three:", 3),
                new SlotData(":four:", 4),
                new SlotData(":five:", 5),
                new SlotData(":six:", 6),
                new SlotData(":seven:", 7),
                new SlotData(":eight:", 8),
                new SlotData(":nine:", 9),
                new SlotData(":tada:", 10),
                new SlotData(":heart:", 11)
        );
    }

    private SlotData slotData1, slotData2, slotData3;
    private boolean isMatched;
    private EmbedBuilder embedBuilder = new EmbedBuilder();
    private long coin;

    public SlotMain(long coin) {
        Collections.shuffle(slotDataList);
        SecureRandom secureRandom = new SecureRandom();

        slotData1 = slotDataList.get(secureRandom.nextInt(slotDataList.size()));
        slotData2 = slotDataList.get(secureRandom.nextInt(slotDataList.size()));
        slotData3 = slotDataList.get(secureRandom.nextInt(slotDataList.size()));

        isMatched = slotData1.getNumber() == slotData2.getNumber() && slotData1.getNumber() == slotData3.getNumber();

        this.coin = coin;

        embedBuilder.setTitle("スロット - " + (isMatched ? "あたり！" : "はずれ！"))
                .setColor(isMatched ? Color.YELLOW : Color.RED)
                .setDescription("当たったら５００倍")
                .addField("結果", slotData1.getName() + " " + slotData2.getName() + " " + slotData3.getName(), false);
    }

    public long resultCoin() {
        return resultCoin(500);
    }

    public long resultCoin(double multiply) {
        return MathUtil.multiply(coin, multiply);
    }
}