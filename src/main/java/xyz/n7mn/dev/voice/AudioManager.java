package xyz.n7mn.dev.voice;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import xyz.n7mn.dev.voice.provider.nico.HimuNicoAudioSourceManager;

import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    public static final AudioPlayerManager manager = new DefaultAudioPlayerManager();
    private static final Map<Long, AudioData> audio = new HashMap<>();

    static {
        manager.registerSourceManager(new HimuNicoAudioSourceManager());
        AudioSourceManagers.registerLocalSource(manager);
        AudioSourceManagers.registerRemoteSources(manager);
    }

    public static AudioData createAudio(AudioType type, GuildMessageChannelUnion textChannel, AudioChannelUnion channel) {
        audio.put(textChannel.getGuild().getIdLong(), new AudioData(type, textChannel, channel));
        return getAudio(textChannel.getGuild());
    }

    public static AudioData getAudio(Guild guild) {
        return audio.get(guild.getIdLong());
    }

    public static void removeAudio(Guild guild) {
        audio.remove(guild.getIdLong());
    }

    public static Map<Long, AudioData> getAudioMap() {
        return audio;
    }

    public static AudioPlayerManager getAudioManager() {
        return manager;
    }
}