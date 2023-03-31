package xyz.n7mn.dev.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlashEventManager {
    private final static List<SlashEvent> listeners = new ArrayList<>();

    public static List<SlashEvent> getListeners() {
        return listeners;
    }

    public static void addListener(SlashEvent... slashListeners) {
        listeners.addAll(Arrays.asList(slashListeners));
    }
}
