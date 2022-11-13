package xyz.n7mn.dev.voice;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import xyz.n7mn.dev.music.MusicManager;
import xyz.n7mn.dev.sqlite.MusicDB;
import xyz.n7mn.dev.sqlite.SQLite;
import xyz.n7mn.dev.sqlite.data.SQLiteMusicData;
import xyz.n7mn.dev.voice.music.MusicScheduler;

import java.util.HashMap;

@Getter @Setter
public class AudioData {
    private AudioType type;
    private TextChannel textChannel;
    private Guild guild;
    private AudioPlayer player;
    private AudioListener listener;
    private VoiceChannel audioChannel;
    private SQLiteMusicData data;

    public AudioData(AudioType type, TextChannel textChannel, VoiceChannel audioChannel) {
        this.type = type;
        this.textChannel = textChannel;
        this.guild = textChannel.getGuild();
        this.player = AudioManager.getAudioManager().createPlayer();
        this.listener = getTypeAudio(type, player);
        this.audioChannel = audioChannel;
        this.data = SQLite.INSTANCE.getMusic().get(guild.getId());
    }


    public boolean setVolume(final int volume) {
        if (volume < 0) {
            return false;
        }
        final boolean canChange = volume <= data.getMaxVolume();

        if (canChange) {
            player.setVolume(volume);
        } else {
            player.setVolume(data.getMaxVolume());
        }

        return canChange;
    }

    public static AudioListener getTypeAudio(AudioType type, AudioPlayer player) {
        if (type == AudioType.MUSIC) {
            return new MusicScheduler(player);
        }
        return null;
    }

    public void remove() {
        AudioManager.removeAudio(guild);
    }
}