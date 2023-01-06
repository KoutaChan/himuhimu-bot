package xyz.n7mn.dev.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.n7mn.dev.message.MessageListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager extends MessageListener {
    private static List<CommandListener> listeners = new ArrayList<>();

    @Override
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        listeners.forEach(data -> {
            Command annotation = data.getClass().getAnnotation(Command.class);

            if (annotation == null) {
                data.onMessageReceivedEvent(event);
            } else {
                
            }
        });
    }

    public static void register(CommandListener listener) {
        listeners.add(listener);
    }
}
