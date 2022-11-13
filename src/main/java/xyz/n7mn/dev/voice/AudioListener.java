package xyz.n7mn.dev.voice;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;

@Getter
public abstract class AudioListener extends AudioEventAdapter {
    private final AudioPlayer player;
    private AudioTrack track;

    public AudioListener(AudioPlayer player) {
        this.player = player;
    }

    public abstract void queue(AudioTrack track);

    public abstract void loadPlay(AudioTrack track);

    public abstract void exit();
}
