package xyz.n7mn.dev.message;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class MessageListener {
    public abstract void onMessageReceivedEvent(MessageReceivedEvent event);
}