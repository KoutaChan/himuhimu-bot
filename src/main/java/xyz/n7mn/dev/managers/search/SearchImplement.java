package xyz.n7mn.dev.managers.search;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class SearchImplement {
    private final Map<Pattern, MethodInfo> listeners = new HashMap<>();
    private final Class<? extends Annotation> search;

    public SearchImplement(Class<? extends Annotation> search) {
        this.search = search;
    }

    public abstract void register(Object object);
    public void search(Object object) {
        if (object.getClass().getAnnotation(search) != null) {
            register(object);
        }
    }

    public Map<Pattern, MethodInfo> getListeners() {
        return listeners;
    }

    public record MethodInfo(Object owner, Method method) {
        public Object getOwner() {
            return owner;
        }

        public Method getMethod() {
            return method;
        }

        public void call(Object event) {
            try {
                method.invoke(this.owner, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}