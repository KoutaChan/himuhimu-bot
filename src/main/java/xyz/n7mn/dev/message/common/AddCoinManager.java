package xyz.n7mn.dev.message.common;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.n7mn.dev.message.MessageListener;
import xyz.n7mn.dev.util.CasinoManager;

public class AddCoinManager extends MessageListener {
    @Override
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        if (event.isFromGuild() && event.getMember() != null) {
            CasinoManager.getCasinoData(event.getGuild().getId(), event.getMember().getId()).addCoin(1);
        }
    }
}