package xyz.n7mn.dev.events;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface ButtonEvent {
    void onButtonInteractionEvent(ButtonInteractionEvent event);
}
