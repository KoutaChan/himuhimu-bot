package xyz.n7mn.dev.command;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import xyz.n7mn.dev.buttonprocessor.Button;
import xyz.n7mn.dev.buttonprocessor.ButtonName;

@ButtonName(componentId = {"help-delete"})
public class HelpButton extends Button {

    @Override
    public void ButtonEvent(ButtonInteractionEvent e) {
        e.getMessage().delete().queue();
    }
}