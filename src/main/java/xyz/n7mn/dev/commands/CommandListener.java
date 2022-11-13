package xyz.n7mn.dev.commands;

import xyz.n7mn.dev.message.MessageListener;

public abstract class CommandListener extends MessageListener {
    private final String displayCommand, help;

    public CommandListener(String displayCommand, String help) {
        this.displayCommand = displayCommand;
        this.help = help;
    }

    public String getDisplayCommand() {
        return displayCommand;
    }

    public String getHelp() {
        return help;
    }
}
