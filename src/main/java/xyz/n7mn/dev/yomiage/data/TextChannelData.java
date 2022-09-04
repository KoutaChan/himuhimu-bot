package xyz.n7mn.dev.yomiage.data;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.TextChannel;

@Getter @Setter
public class TextChannelData {

    final TextChannel textChannel;
    boolean first = true;

    public TextChannelData(TextChannel textChannel) {
        this.textChannel = textChannel;
    }
}