package xyz.n7mn.dev.events;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashEvent {
    void onSlashCommandInteractionEvent(SlashCommandInteractionEvent event);
}
