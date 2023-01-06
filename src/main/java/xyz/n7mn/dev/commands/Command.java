package xyz.n7mn.dev.commands;

public @interface Command {
    String[] command();

    boolean onlyGuildCommand() default true;
}
