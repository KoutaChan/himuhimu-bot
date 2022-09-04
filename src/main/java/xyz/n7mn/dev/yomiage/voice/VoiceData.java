package xyz.n7mn.dev.yomiage.voice;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import xyz.n7mn.dev.music.AudioHandler;

@Getter @Setter
public class VoiceData {
    private final AudioPlayer audioPlayer;
    private final VoiceTrackScheduler trackScheduler;
    private final Guild guild;

    /**
     * @param audioPlayer
     * @author - https://www.youtube.com/watch?v=wdBP3twRjl8
     */

    //lava playerよくわからないので上のリンクを参考
    public VoiceData(AudioPlayer audioPlayer, Guild guild) {
        this.audioPlayer = audioPlayer;
        this.trackScheduler = new VoiceTrackScheduler(this);
        audioPlayer.addListener(trackScheduler);

        this.guild = guild;
    }

    public AudioHandler getAudioHandler() {
        return new AudioHandler(audioPlayer);
    }

    public synchronized void playTrack(TrackData track) {
        trackScheduler.queue(track);
    }
}
