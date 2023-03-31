package xyz.n7mn.dev.events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface MessageEvent {
    void onMessageReceivedEvent(MessageReceivedEvent event);
}