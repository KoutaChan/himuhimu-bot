package xyz.n7mn.dev.commands;

import java.util.Collection;
import java.util.List;

public @interface Command {
    String[] command();

    boolean onlyGuildCommand() default true;
}
