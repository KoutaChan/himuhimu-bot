package xyz.n7mn.dev;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import xyz.n7mn.dev.events.ButtonEventManager;
import xyz.n7mn.dev.events.EntitySelectMenuEventManager;
import xyz.n7mn.dev.events.MessageEventManager;
import xyz.n7mn.dev.events.SlashEventManager;
import xyz.n7mn.dev.util.DiscordUtil;
import xyz.n7mn.dev.voice.AudioData;
import xyz.n7mn.dev.voice.AudioManager;

public class EventListener extends ListenerAdapter {
    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if (event.getJDA().getSelfUser().equals(event.getMember())) {
            DiscordUtil.stop(event.getGuild());
            return;
        }
        AudioData data = AudioManager.getAudio(event.getGuild());
        AudioChannel connectedChannel = data != null ? data.getAudioChannel() : getCurrentAudioChannel(event.getGuild());
        //TODO: 多分 connectedChannelがnullのときは実行しなくてもいい
        if (getConnectedUsers(connectedChannel) == 0) {
            DiscordUtil.stop(event.getGuild());
        }
    }

    private AudioChannel getCurrentAudioChannel(Guild guild) {
        if (!guild.getAudioManager().isConnected()) {
            return null;
        }
        return guild.getAudioManager().getConnectedChannel();
    }

    private int getConnectedUsers(AudioChannel channel) {
        if (channel == null) {
            return 0;
        }
        int value = 0;
        for (Member member : channel.getMembers()) {
            if (member.getUser().isBot()) {
                continue;
            }
            value++;
        }
        return value;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.isWebhookMessage() || event.getAuthor().isBot()) {
            return;
        }
        MessageEventManager.getListeners().forEach(v -> {
            v.onMessageReceivedEvent(event);
        });
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) {
            SlashEventManager.getListeners().forEach(v -> {
                v.onSlashCommandInteractionEvent(event);
            });
        }
    }

    @Override
    public void onEntitySelectInteraction(EntitySelectInteractionEvent event) {
        if (event.isFromGuild()) {
            EntitySelectMenuEventManager.getListeners().forEach(v -> {
                v.onEntitySelectMenuEvent(event);
            });
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.isFromGuild()) {
            ButtonEventManager.getListeners().forEach(v -> {
                v.onButtonInteractionEvent(event);
            });
        }
    }
}