package xyz.n7mn.dev.yomiage.voice;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
@Getter
@Setter
public class VoiceTrackScheduler extends AudioEventAdapter {
    private final VoiceData player;
    private final BlockingQueue<TrackData> queue = new LinkedBlockingQueue<>();
    private TrackData last;

    /**
     * @param player The audio player this scheduler uses
     */
    public VoiceTrackScheduler(VoiceData player) {
        this.player = player;
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(TrackData track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.

        if (!player.getAudioPlayer().startTrack(track.getAudioTrack(), true)) {
            queue.offer(track);
        } else {
            last = track;
        }
    }

    public void clearQueue() {
        queue.clear();
    }

    public int queueSize() {
        return queue.size();
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.

        TrackData trackData = queue.poll();

        last = trackData;
        if (trackData == null) {
            player.getAudioPlayer().stopTrack();
        } else {
            player.getAudioPlayer().startTrack(trackData.getAudioTrack(), false);
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)

        Path wav = Paths.get(last.getFile().getPath());
        Path txt = Paths.get(last.getFile().getPath().replaceFirst("wav$", "txt"));
        try {
            wav.toFile().delete();
            txt.toFile().delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}