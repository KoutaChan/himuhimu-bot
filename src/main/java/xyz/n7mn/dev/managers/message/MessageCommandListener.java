package xyz.n7mn.dev.managers.message;

import xyz.n7mn.dev.events.MessageEvent;

public abstract class MessageCommandListener implements MessageEvent {
    private final String displayCommand, help;

    public MessageCommandListener(String displayCommand, String help) {
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
