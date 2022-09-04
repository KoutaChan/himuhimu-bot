package xyz.n7mn.dev.yomiage.data;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;

@Getter
@Setter
public class VoiceRoidData {
    private Guild guild;
    private TextChannel textChannel;
    private File file;
    private String data;
    private boolean isEnded = false;

    public VoiceRoidData(File file, String data) {
        this.file = file;
        this.data = data;
    }

    public VoiceRoidData(TextChannel textChannel, File file, String data) {
        this.guild = textChannel.getGuild();
        this.textChannel = textChannel;
        this.file = file;
        this.data = data;
    }
}
