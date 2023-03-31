package xyz.n7mn.dev.managers.message;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.n7mn.dev.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;

public class MessageCommandManager implements MessageEvent {
    private final static List<MessageCommandListener> listeners = new ArrayList<>();

    @Override
    public void onMessageReceivedEvent(MessageReceivedEvent event) {

    }

    public static void register(MessageCommandListener listener) {
        listeners.add(listener);
    }
}
