package xyz.n7mn.dev.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageEventManager {
    private final static List<MessageEvent> listeners = new ArrayList<>();

    public static List<MessageEvent> getListeners() {
        return listeners;
    }

    public static void addListener(MessageEvent... messageListeners) {
        listeners.addAll(Arrays.asList(messageListeners));
    }
}