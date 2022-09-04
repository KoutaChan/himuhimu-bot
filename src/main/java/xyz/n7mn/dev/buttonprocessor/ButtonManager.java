package xyz.n7mn.dev.buttonprocessor;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import xyz.n7mn.dev.command.HelpButton;
import xyz.n7mn.dev.command.casino.Button.BlackJackButton;
import xyz.n7mn.dev.command.music.Button.MusicPlayButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ButtonManager {
    public static final List<Class<? extends Button>> button = new ArrayList<>();

    static {
        Collections.addAll(
                button,
                BlackJackButton.class,
                HelpButton.class,
                MusicPlayButton.class
        );
    }

    public static void execute(ButtonInteractionEvent e) {
        try {
            for (Class<? extends Button> button : button) {
                ButtonName annotation = button.getAnnotation(ButtonName.class);

                for (String componentId : annotation.componentId()) {
                    if (e.getComponentId().equals(componentId)) {
                        Button instance = button.newInstance();
                        instance.ButtonEvent(e);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}