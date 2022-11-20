package xyz.n7mn.dev.voice;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;

@Getter
public abstract class AudioListener extends AudioEventAdapter {
    protected final AudioPlayer player;
    protected AudioTrack track;

    public AudioListener(AudioPlayer player) {
        this.player = player;
    }

    public abstract void queue(AudioTrack track);

    public abstract void load(AudioTrack track);

    public abstract void searchAndPlay(String url);

    public abstract void exit();
}