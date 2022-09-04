package xyz.n7mn.dev.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@CommandName(command = {"h.ping"}, channelType = ChannelType.TEXT, commandType = CommandType.GENERAL, help = "BOTの応答時間を返します")
public class PingCommand extends Command {
    @Override
    public void CommandEvent(DiscordData e) {
        long sys = System.currentTimeMillis();
        e.getMessage().reply("応答したよっ\n(情報取得中...)").queue(
                message -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    Date fromDate = Date.from(e.getMessage().getTimeCreated().toInstant());
                    Date toDate = Date.from(message.getTimeCreated().toInstant());

                    long now = System.currentTimeMillis() - sys;

                    EmbedBuilder builder = new EmbedBuilder().setColor(Color.LIGHT_GRAY)
                            .addField("h.pingが送信された日時", sdf.format(fromDate),false)
                            .addField("ひむひむちゃんが送信した日時", sdf.format(toDate), false)
                            .addField("h.pingが送信されてから応答するまでの時間1", now + "ms", false)
                            .addField("h.pingが送信されてから応答するまでの時間2", (toDate.getTime() - fromDate.getTime()) + "ms", false);
                    message.editMessage("応答したよっ").setEmbeds(builder.build()).queue();

                }
        );
    }
}
