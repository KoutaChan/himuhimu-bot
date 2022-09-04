package xyz.n7mn.dev.command.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;

import java.awt.*;

@CommandName(command = {"h.color"}, channelType = ChannelType.TEXT, commandType = CommandType.FUN, help = "%command% {hex-code}")
public class ColorCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        try {
            final Color color = Color.decode(e.getArgs()[1]);
            final EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("カラーコード" + e.getArgs()[1] + "の詳細")
                    .setDescription(e.getArgs()[1])
                    .setColor(color)
                    .addField("RGB Color", String.format("[R] %s\n[G] %s\n[B] %s", color.getRed(), color.getGreen(), color.getBlue()), false);

            e.getTextChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        } catch (Exception ex) {
            e.getMessage().replyEmbeds(new EmbedBuilder()
                    .setTitle("エラーが発生しました！")
                    .setColor(Color.RED)
                    .addField("エラー内容", ex.getMessage(), false)
                    .build()).queue();
        }
    }
}