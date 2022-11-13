package xyz.n7mn.dev.voice;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    public static final AudioPlayerManager manager = new DefaultAudioPlayerManager();

    static {
        AudioSourceManagers.registerLocalSource(manager);
        AudioSourceManagers.registerRemoteSources(manager);
    }

    private final static Map<Long, AudioData> audio = new HashMap<>();

    public void createAudio(AudioType type, TextChannel textChannel, VoiceChannel channel) {
        audio.put(textChannel.getGuild().getIdLong(), new AudioData(type, textChannel, channel));
    }

    public static AudioData getAudio(Guild guild) {
        return audio.get(guild.getIdLong());
    }

    public static AudioData removeAudio(Guild guild) {
        return audio.remove(guild.getIdLong());
    }

    public static Map<Long, AudioData> getAudioMap() {
        return audio;
    }

    public static AudioPlayerManager getAudioManager() {
        return manager;
    }
}