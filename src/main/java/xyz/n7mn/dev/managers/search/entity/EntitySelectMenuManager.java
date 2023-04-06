package xyz.n7mn.dev.managers.search.entity;

import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import xyz.n7mn.dev.events.EntitySelectMenuEvent;
import xyz.n7mn.dev.events.EntitySelectMenuEventManager;
import xyz.n7mn.dev.managers.search.SearchImplement;
import xyz.n7mn.dev.managers.search.button.ButtonManager;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class EntitySelectMenuManager extends SearchImplement implements EntitySelectMenuEvent {
    public EntitySelectMenuManager() {
        super(UseEntitySelectMenu.class);
        EntitySelectMenuEventManager.addListener(this);
    }

    /**
     * from - {@link ButtonManager}
     * @param event - イベント
     */
    @Override
    public void onEntitySelectMenuEvent(EntitySelectInteractionEvent event) {
        getListeners().entrySet().stream().filter(it -> it.getKey().matcher(event.getComponentId()).matches()).forEachOrdered(aiming -> aiming.getValue().call(event));
    }

    @Override
    public void register(Object object) {
        for (Method method : object.getClass().getMethods()) {
            EntitySelectMenuInteract call = method.getAnnotation(EntitySelectMenuInteract.class);
            if (call == null) {
                continue;
            }
            if (call.regex() != null) {
                getListeners().put(Pattern.compile(call.regex()), new MethodInfo(object, method));
            }
        }
    }
}
