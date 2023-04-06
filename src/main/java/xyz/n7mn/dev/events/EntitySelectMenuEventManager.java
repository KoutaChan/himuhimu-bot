package xyz.n7mn.dev.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntitySelectMenuEventManager {
    private final static List<EntitySelectMenuEvent> listeners = new ArrayList<>();

    public static List<EntitySelectMenuEvent> getListeners() {
        return listeners;
    }

    public static void addListener(EntitySelectMenuEvent... selectMenuEvents) {
        listeners.addAll(Arrays.asList(selectMenuEvents));
    }
}
