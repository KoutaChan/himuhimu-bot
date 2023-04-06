package xyz.n7mn.dev.managers.search.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EntitySelectMenuInteract {
    String regex();
}
