package xyz.n7mn.dev.slashcommands;

public @interface SlashCommand {
    String command();

    boolean onlyGuildCommand() default true;
}
