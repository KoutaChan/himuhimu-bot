package xyz.n7mn.dev.commands;

import java.util.Collection;

public @interface Command {
    Collection<String> command();

    boolean onlyGuildCommand() default true;
}
