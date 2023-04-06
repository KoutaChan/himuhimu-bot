package xyz.n7mn.dev.events;

import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;

public interface EntitySelectMenuEvent {
    void onEntitySelectMenuEvent(EntitySelectInteractionEvent event);
}