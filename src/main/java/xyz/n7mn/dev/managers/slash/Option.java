package xyz.n7mn.dev.managers.slash;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Option {
    OptionType type();
    String name();
    String description() default "None";
    boolean required() default true;
    boolean autoComplete() default false;
    StringChoice[] stringChoices() default {};
}
