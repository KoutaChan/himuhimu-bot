package xyz.n7mn.dev.managers.button;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import xyz.n7mn.dev.events.ButtonEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ButtonManager implements ButtonEvent {
    private final static Map<Pattern, ButtonMethodInfo> listeners = new HashMap<>();

    @Override
    public void onButtonInteractionEvent(ButtonInteractionEvent event) {
        listeners.entrySet().stream().filter(it -> it.getKey().matcher(event.getComponentId()).matches())
                .forEachOrdered(aiming -> aiming.getValue().call(event));
    }

    public static void register(Object object) {
        for (Method method : object.getClass().getMethods()) {
            ButtonInteract call = method.getAnnotation(ButtonInteract.class);
            if (call != null) {
                listeners.put(Pattern.compile(call.regex()), new ButtonMethodInfo(object, method));
            }
        }
    }

    public static void search(Object object) {
        if (object.getClass().getAnnotation(UseButton.class) != null) {
            register(object);
        }
    }

    public record ButtonMethodInfo(Object owner, Method method) {
        public Object getOwner() {
            return owner;
        }

        public Method getMethod() {
            return method;
        }

        public void call(ButtonInteractionEvent event) {
            try {
                method.invoke(this.owner, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}