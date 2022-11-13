package xyz.n7mn.dev.voice.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import xyz.n7mn.dev.music.AudioTrackData;
import xyz.n7mn.dev.voice.AudioListener;

public class MusicScheduler extends AudioListener {
    public MusicScheduler(AudioPlayer player) {
        super(player);
    }

    @Override
    public void queue(AudioTrack track) {

    }

    @Override
    public void loadPlay(AudioTrack track) {

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        super.onTrackEnd(player, track, endReason);
    }

    @Override
    public void exit() {

    }
}