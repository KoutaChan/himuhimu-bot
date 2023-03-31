package xyz.n7mn.dev.managers.slash;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SlashCommand {
    String name();
    String description() default "-";
    Option[] options() default @Option(type = OptionType.UNKNOWN, name = "2.0 Implement");
    CommandType type() default CommandType.OTHER;
    SubCommandType commandType() default SubCommandType.NONE;
}