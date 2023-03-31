package xyz.n7mn.dev.events.common;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.n7mn.dev.events.MessageEvent;
import xyz.n7mn.dev.util.CasinoManager;

public class AddCoinManager implements MessageEvent {
    @Override
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        if (event.isFromGuild() && event.getMember() != null) {
            CasinoManager.getCasinoData(event.getGuild().getId(), event.getMember().getId()).addCoin(1);
        }
    }
}