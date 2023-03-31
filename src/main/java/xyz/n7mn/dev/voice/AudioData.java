package xyz.n7mn.dev.voice;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import xyz.n7mn.dev.sqlite.data.SQLiteMusicData;
import xyz.n7mn.dev.util.MusicUtil;
import xyz.n7mn.dev.voice.music.MusicScheduler;
import xyz.n7mn.dev.voice.roid.RoidScheduler;

@Getter @Setter
public class AudioData {
    private AudioType type;
    private GuildMessageChannelUnion messageChannel;
    private Guild guild;
    private AudioPlayer player;
    private AudioListener listener;
    private AudioChannelUnion audioChannel;
    private SQLiteMusicData data;
    private AudioHandler handler;

    public AudioData(AudioType type, GuildMessageChannelUnion messageChannel, AudioChannelUnion audioChannel) {
        this.type = type;
        this.messageChannel = messageChannel;
        this.guild = messageChannel.getGuild();
        this.player = AudioManager.getAudioManager().createPlayer();
        this.listener = getTypeAudio(type, player);
        this.audioChannel = audioChannel;
        this.data = MusicUtil.getMusicData(guild);
        this.handler = new AudioHandler(player);
        player.addListener(listener);

        guild.getAudioManager().openAudioConnection(audioChannel);
        guild.getAudioManager().setSendingHandler(handler);
    }

    public int getVolume() {
        return player.getVolume();
    }

    public int getMaxVolume() {
        return data.getMaxVolume();
    }

    public boolean setVolume(final int volume) {
        if (volume < 0) {
            player.setVolume(0);
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
            return new MusicScheduler(AudioManager.getAudioManager(), player);
        } else if (type == AudioType.VOICE_ROID) {
            return new RoidScheduler(player);
        }
        return null;
    }

    public AudioChannelUnion getAudioChannel() {
        AudioChannelUnion channelUnion = guild.getAudioManager().getConnectedChannel();
        return channelUnion == null ? this.audioChannel : channelUnion;
    }

    public void remove() {
        guild.getAudioManager().closeAudioConnection();
        AudioManager.removeAudio(guild);
    }
}