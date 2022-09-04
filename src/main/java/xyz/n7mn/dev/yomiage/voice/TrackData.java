package xyz.n7mn.dev.yomiage.voice;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@Getter @Setter
public class TrackData {
    private AudioTrack audioTrack;
    private File file;

    public TrackData(@NotNull AudioTrack audioTrack, @NotNull File file) {
        this.audioTrack = audioTrack;
        this.file = file;
    }
}