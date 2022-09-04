package xyz.n7mn.dev.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import xyz.n7mn.dev.sqlite.data.SQLiteMusicData;

@Getter @Setter
public class MusicData {
    private final AudioPlayer audioPlayer;
    private final TrackScheduler trackScheduler;
    private final Guild guild;

    /**
     * @param audioPlayer
     * @author - https://www.youtube.com/watch?v=wdBP3twRjl8
     */

    //lava playerよくわからないので上のリンクを参考
    public MusicData(AudioPlayer audioPlayer, Guild guild) {
        this.audioPlayer = audioPlayer;
        this.trackScheduler = new TrackScheduler(this);
        audioPlayer.addListener(trackScheduler);

        this.guild = guild;
    }

    public boolean setVolume(SQLiteMusicData musicData, int volume) {
        final boolean allowed = volume >= 0 && volume <= musicData.getMaxVolume();

        if (allowed) audioPlayer.setVolume(volume);
        else audioPlayer.setVolume(musicData.getMaxVolume());

        return allowed;
    }

    public AudioHandler getAudioHandler() {
        return new AudioHandler(audioPlayer);
    }

    public synchronized void playTrack(AudioTrackData track) {
        trackScheduler.queue(track);
    }
}
