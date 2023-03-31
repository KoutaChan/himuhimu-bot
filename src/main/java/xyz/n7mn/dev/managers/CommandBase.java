package xyz.n7mn.dev.managers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import xyz.n7mn.dev.managers.button.ButtonManager;
import xyz.n7mn.dev.util.DiscordUtil;
import xyz.n7mn.dev.voice.AudioData;
import xyz.n7mn.dev.voice.AudioManager;

public class CommandBase {
    public CommandBase() {
        ButtonManager.search(this);
    }

    public Member getMember(Guild guild, User user) {
        return user.getMutualGuilds().contains(guild) ? guild.retrieveMember(user).complete() : null;
    }

    public AudioChannelUnion getConnectedVoiceChannel(Member member) {
        return member.getVoiceState().getChannel();
    }

    public AudioData getAudioData(Guild guild) {
        return AudioManager.getAudio(guild);
    }

    public EmbedBuilder getErrorEmbeds(String reason) {
        return DiscordUtil.getErrorEmbeds(reason);
    }

    public String getChannel(String channel) {
        return channel.replaceFirst("<", "").replaceFirst(">", "");
    }
}
