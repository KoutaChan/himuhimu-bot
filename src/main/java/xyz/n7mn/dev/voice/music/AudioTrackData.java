package xyz.n7mn.dev.voice.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

public record AudioTrackData(MusicType type, AudioTrack track, Consumer<AudioTrack> onTracking) {
    public AudioTrack getTrack() {
        return track;
    }
}
