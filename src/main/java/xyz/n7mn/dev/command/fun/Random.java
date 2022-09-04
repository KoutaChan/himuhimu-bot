package xyz.n7mn.dev.command.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@CommandName(command = {"h.random"}, channelType = ChannelType.TEXT, commandType = CommandType.FUN, help = "%command% あ い う などの文字を指定するとランダムで1個を抽選します")
public class Random extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        final List<String> list = new ArrayList<>(Arrays.asList(e.getArgs()));

        list.remove(0);
        if (list.size() == 0) {
            e.getMessage().reply("おかしいですね？ 何も選ばれませんでした！").queue();
        } else {
            Collections.shuffle(list);

            e.getMessage().replyEmbeds(new EmbedBuilder()
                    .setColor(Color.PINK)
                    .addField("結果: ", list.get(0) + " が選ばれました", false).build()).queue();
        }
    }
}