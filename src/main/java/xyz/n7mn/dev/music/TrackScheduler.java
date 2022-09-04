package xyz.n7mn.dev.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
import lombok.Setter;
import xyz.n7mn.dev.music.custom.NicoVideo;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
@Getter
@Setter
public class TrackScheduler extends AudioEventAdapter {
    private final MusicData player;
    private final BlockingQueue<AudioTrackData> queue = new LinkedBlockingQueue<>();
    private boolean isRepeated;
    public AudioTrackData audioTrack;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(MusicData player) {
        this.player = player;
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrackData track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.

        boolean isStarted = player.getAudioPlayer().startTrack(track.getAudioTrack(), true);

        if (isStarted) {
            if (track.getMusicType() == MusicType.NICONICO) {
                new Thread(() -> track.getNicoVideo().heart_beat()).start();
            }
            audioTrack = track;

            System.out.printf("新しいミュージックを再生します: %s ミュージックタイプ:%s%n", track.getTitle(), track.getMusicType());
        } else {
            queue.offer(track);
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

        if (audioTrack.getMusicType() == MusicType.NICONICO) {
            audioTrack.getNicoVideo().setBreak(true);
        }

        audioTrack = queue.poll();
        if (audioTrack == null) {
            player.getAudioPlayer().stopTrack();
            return;
        }

        if (audioTrack.getMusicType() == MusicType.NICONICO) {
            loadAndPlay(audioTrack, false);
        } else {
            player.getAudioPlayer().startTrack(audioTrack.getAudioTrack(), false);
        }

        System.out.printf("新しいミュージックを再生します: %s ミュージックタイプ:%s%n", audioTrack.getTitle(), audioTrack.getMusicType());
    }

    public void nextTrack(AudioTrackData audioTrack) {
        this.audioTrack = audioTrack;
        player.getAudioPlayer().startTrack(audioTrack.getAudioTrack().makeClone(), false);

        System.out.printf("ミュージックをリピートします: %s ミュージックタイプ:%s%n", audioTrack.getTitle(), audioTrack.getMusicType());
    }


    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.equals(AudioTrackEndReason.FINISHED)) {
            if (isRepeated) {
                nextTrack(this.audioTrack);
            }
        }

        if (endReason.mayStartNext) {
            if (isRepeated) {
                nextTrack(this.audioTrack);
            } else {
                nextTrack();
            }
        }
    }

    public void loadAndPlay(AudioTrackData data, final boolean queue) {
        MusicData musicManager = MusicManager.getMusicManager().getMusicData(player.getGuild());

        if (data.getMusicType() == MusicType.NICONICO) {
            NicoVideo nicoVideo = new NicoVideo(data.getUrl(), true);

            if (nicoVideo.isSuccess()) {
                data.setNicoVideo(nicoVideo);

                data.setURL();
            }
        }

        player.getGuild().getAudioManager().setSendingHandler(musicManager.getAudioHandler());

        MusicManager.manager.loadItemOrdered(musicManager, data.getUrl(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                data.setAudioTrack(track);
                add(data, queue);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
            }

            @Override
            public void noMatches() {
                //見つからなかった
            }

            @Override
            public void loadFailed(FriendlyException e) {
                //ロード時にエラー発生
            }
        });
    }

    public void add(AudioTrackData data, final boolean queue) {
        if (queue) {
            queue(data);
        } else {
            nextTrack(data);
        }
    }
}