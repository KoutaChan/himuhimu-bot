package xyz.n7mn.dev.command.admin;

import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.DiscordData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreventToken extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        String sync = "[a-zA-Z0-9][.][a-zA-Z][a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9].[a-zA-Z][.][a-zA-Z0-9]";

        Matcher mather = Pattern.compile(sync).matcher(e.getMessageText());
        if (mather.find()) {
            e.getMessage().delete().reason("不可解なメッセージまたはトークン").queue();
        }
    }
}