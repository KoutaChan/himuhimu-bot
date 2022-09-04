package xyz.n7mn.dev.yomiage.voice;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import xyz.n7mn.dev.yomiage.data.VoiceRoidData;

import java.util.HashMap;
import java.util.Map;

public class VoiceManager {
    public static final AudioPlayerManager manager = new DefaultAudioPlayerManager();
    @Getter
    private static final VoiceManager voiceManager = new VoiceManager();
    public static Map<Long, VoiceData> voiceData = new HashMap<>();

    public VoiceManager() {
        AudioSourceManagers.registerLocalSource(manager);
        AudioSourceManagers.registerRemoteSources(manager);
    }

    public synchronized VoiceData getVoiceData(Guild guild) {
        if (!voiceData.containsKey(guild.getIdLong())) createVoiceData(guild);
        return voiceData.get(guild.getIdLong());
    }

    public synchronized void createVoiceData(Guild guild) {
        voiceData.put(guild.getIdLong(), new VoiceData(manager.createPlayer(), guild));
    }

    public synchronized boolean hasVoiceData(Guild guild) {
        return voiceData.containsKey(guild.getIdLong());
    }

    public void loadAndPlay(VoiceRoidData e) {
        VoiceData musicManager = VoiceManager.getVoiceManager().getVoiceData(e.getTextChannel().getGuild());
        e.getTextChannel().getGuild().getAudioManager().setSendingHandler(musicManager.getAudioHandler());

        new Thread(() -> {
            while (true) {
                if (e.getFile().exists() && e.getFile().canRead() && e.isEnded()) {
                    AudioTrack audioTrack = (AudioTrack) new LocalAudioSourceManager().loadItem(manager, new AudioReference(e.getFile().getPath(), null, null));

                    musicManager.playTrack(new TrackData(audioTrack, e.getFile()));

                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
}