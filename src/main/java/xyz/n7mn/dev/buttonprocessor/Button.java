package xyz.n7mn.dev.buttonprocessor;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public abstract class Button {
    public abstract void ButtonEvent(ButtonInteractionEvent e);
}