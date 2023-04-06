package xyz.n7mn.dev.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ButtonEventManager {
    private final static List<ButtonEvent> listeners = new ArrayList<>();

    public static List<ButtonEvent> getListeners() {
        return listeners;
    }

    public static void addListener(ButtonEvent... buttonEvents) {
        listeners.addAll(Arrays.asList(buttonEvents));
    }
}
