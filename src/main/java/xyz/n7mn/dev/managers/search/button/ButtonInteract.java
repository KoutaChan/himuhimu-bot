package xyz.n7mn.dev.managers.search.button;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ButtonInteract {
    String regex();
}
