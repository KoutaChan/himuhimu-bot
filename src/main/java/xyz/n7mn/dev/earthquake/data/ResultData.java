package xyz.n7mn.dev.earthquake.data;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;

@Getter @Setter
public class ResultData {
    //todo: remove!

    public ResultData(EmbedBuilder embedBuilder, EmbedBuilder temp) {
        this.embedBuilder = embedBuilder;
        this.temp = temp;
    }

    public ResultData(EmbedBuilder embedBuilder, EmbedBuilder temp, byte[] data, final boolean isGIF) {
        this.embedBuilder = embedBuilder;
        this.temp = temp;
        this.data = data;
        this.isGIF = isGIF;
    }

    private boolean isGIF;
    private byte[] data;
    private EmbedBuilder embedBuilder;
    private EmbedBuilder temp;
}
