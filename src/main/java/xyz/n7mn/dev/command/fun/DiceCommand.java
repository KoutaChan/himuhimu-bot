package xyz.n7mn.dev.command.fun;

import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;

import java.security.SecureRandom;

@CommandName(command = {"h.dice"}, channelType = ChannelType.TEXT, commandType = CommandType.FUN, help = "1から6のランダムな数字を生成します\n%command% 1 15 などの数字を指定すると 1から15の乱数を生成します")
public class DiceCommand extends Command {
    @Override
    public void CommandEvent(DiscordData e) {
        SecureRandom secureRandom = new SecureRandom();

        if (e.getArgs().length == 1) {

            final int i = (secureRandom.nextInt(Integer.MAX_VALUE) % 6) + 1;

            e.getTextChannel().sendMessage("さいころの結果は" + i + "です。").queue();
        } else if (e.getArgs().length == 3) {
            try {

                int x = Integer.parseInt(e.getArgs()[1]), y = Integer.parseInt(e.getArgs()[2]);

                if (y < x) {
                    y = Integer.parseInt(e.getArgs()[1]);
                    x = Integer.parseInt(e.getArgs()[2]);
                }

                final int i = secureRandom.nextInt((y - x) + 1) + x;

                e.getTextChannel().sendMessage("さいころの結果は" + i + "です。").queue();
            } catch (Exception exception) {
                e.getMessage().reply("エラーが発生しました！").queue();
            }
        }
    }
}