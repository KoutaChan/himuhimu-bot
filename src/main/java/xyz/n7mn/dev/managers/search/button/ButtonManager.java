package xyz.n7mn.dev.managers.search.button;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import xyz.n7mn.dev.events.ButtonEvent;
import xyz.n7mn.dev.events.ButtonEventManager;
import xyz.n7mn.dev.managers.search.SearchImplement;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class ButtonManager extends SearchImplement implements ButtonEvent {
    public ButtonManager() {
        super(UseButton.class);
        ButtonEventManager.addListener(this);
    }

    @Override
    public void onButtonInteractionEvent(ButtonInteractionEvent event) {
        getListeners().entrySet().stream().filter(it -> it.getKey().matcher(event.getComponentId()).matches())
                .forEachOrdered(aiming -> aiming.getValue().call(event));
    }

    @Override
    public void register(Object object) {
        for (Method method : object.getClass().getMethods()) {
            ButtonInteract call = method.getAnnotation(ButtonInteract.class);
            if (call == null) {
                continue;
            }
            if (call.regex() != null) {
                getListeners().put(Pattern.compile(call.regex()), new MethodInfo(object, method));
            }
        }
    }
}