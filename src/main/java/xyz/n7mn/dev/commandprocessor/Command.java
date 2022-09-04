package xyz.n7mn.dev.commandprocessor;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

@Getter
@Setter
public abstract class Command {

    public abstract void CommandEvent(DiscordData e);
    
    public boolean isAudioConnected(Guild guild) {
        return guild.getAudioManager().getConnectionStatus() == ConnectionStatus.CONNECTING_AWAITING_READY
                || guild.getAudioManager().getConnectionStatus() == ConnectionStatus.CONNECTING_ATTEMPTING_UDP_DISCOVERY
                || guild.getAudioManager().getConnectionStatus() == ConnectionStatus.CONNECTING_AWAITING_WEBSOCKET_CONNECT
                || guild.getAudioManager().getConnectionStatus() == ConnectionStatus.CONNECTING_AWAITING_AUTHENTICATION
                || guild.getAudioManager().getConnectionStatus() == ConnectionStatus.CONNECTING_AWAITING_ENDPOINT
                || guild.getAudioManager().getConnectionStatus() == ConnectionStatus.CONNECTED;
    }

    public boolean isSameAudio(Guild guild, User user) {
        return guild.getMember(user).getVoiceState().getChannel() == guild.getAudioManager().getConnectedChannel();
    }
}