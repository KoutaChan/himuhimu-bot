package xyz.n7mn.dev.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageListeners {
    private static List<MessageListener> listeners = new ArrayList<>();

    public static List<MessageListener> getListeners() {
        return listeners;
    }

    public static void addListener(MessageListener... messageListeners) {
        listeners.addAll(Arrays.asList(messageListeners));
    }
}