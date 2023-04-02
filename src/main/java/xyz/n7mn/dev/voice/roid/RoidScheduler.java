package xyz.n7mn.dev.voice.roid;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import xyz.n7mn.dev.voice.AudioListener;
import xyz.n7mn.dev.voice.roid.ai.CeVIOAIProvider;
import xyz.n7mn.dev.voice.roid.creative.CeVIOCreativeProvider;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RoidScheduler extends AudioListener {
    public static void main(String[] args) {

    }

    private final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();
    private final static Map<RoidHostSelector, RoidProvider> provider = new HashMap<>() {{
        put(RoidHostSelector.CEVIO_AI, new CeVIOAIProvider());
        put(RoidHostSelector.CEVIO_CREATIVE, new CeVIOCreativeProvider());
    }};

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            provider.values().forEach(RoidProvider::stop);
        }));
    }

    public RoidScheduler(AudioPlayer player) {
        super(player);
    }


    @Override
    public void queue(AudioTrack track) {
        boolean started = player.startTrack(track, true);

        if (!started) {
            this.track = track;
        } else {
            queue.add(track);
        }
    }

    @Override
    public void load(AudioTrack track) {
        this.track = track;
        player.startTrack(track, true);
    }

    @Override
    public void searchAndPlay(String url) {
        throw new IllegalStateException("Not Supporting searchAndPlay");
    }

    public void queue(@NotNull final User user, @NotNull String text) {

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            //load(queue.poll());
        }
    }

    @Override
    public AudioTrack stopCurrentTrack() {
        AudioTrack track = player.getPlayingTrack();
        if (track != null) {
            player.destroy();
        }
        return track;
    }

    @Override
    public List<AudioTrack> skip(int size) {
        Iterator<AudioTrack> audioTrack = queue.iterator();
        if (player.getPlayingTrack() == null) {
            return Collections.emptyList();
        }
        List<AudioTrack> tracks = new ArrayList<>();
        tracks.add(stopCurrentTrack());
        for (int i = 0; i < size - 1; i++) {
            if (audioTrack.hasNext()) {
                tracks.add(audioTrack.next());
                audioTrack.remove();
            }
        }
        return tracks;
    }

    @Override
    public void exit() {
        track.stop();
    }
}