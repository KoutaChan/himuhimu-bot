package xyz.n7mn.dev.voice.roid;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import xyz.n7mn.dev.voice.AudioListener;

public class RoidScheduler extends AudioListener {
    public RoidScheduler(AudioPlayer player) {
        super(player);
    }

    @Override
    public void queue(AudioTrack track) {

    }

    @Override
    public void loadPlay(AudioTrack track) {

    }

    @Override
    public void exit() {

    }
}
