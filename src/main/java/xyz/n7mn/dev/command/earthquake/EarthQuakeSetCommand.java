package xyz.n7mn.dev.command.earthquake;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;
import xyz.n7mn.dev.sqlite.SQLite;

import java.awt.*;

@CommandName(command = {"h.earthquakeset", "h.eqs"}, channelType = ChannelType.TEXT, commandType = CommandType.EARTHQUAKE, permission = {Permission.ADMINISTRATOR, Permission.MESSAGE_MANAGE},help = "地震情報を送るチャンネルを指定します\n%command% <チャンネルID> で登録。\n%command% reset でリセット可能です。")
public class EarthQuakeSetCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        if (e.getArgs().length > 1) {
            if (!e.getArgs()[1].equalsIgnoreCase("reset")) {
                String channel = e.getArgs()[1].replaceAll("[^\\d]", "");
                TextChannel textChannel = channel.isEmpty() ? null : e.getGuild().getTextChannelById(channel);

                if (textChannel == null) {
                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setTitle("エラーが発生しましたっ！")
                            .setColor(Color.PINK)
                            .setDescription("本当にチャンネルを指定していますか？\n次の画像通りにしてみるか\nBOTにすべての権限を付与してみてください！")
                            .setImage("https://i.imgur.com/0jKYMk8.png");
                    e.getTextChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                } else {
                    String[] earthQuake = SQLite.INSTANCE.getEarthQuake().get(e.getGuild().getId());
                    if (earthQuake == null) {
                        SQLite.INSTANCE.getEarthQuake().insert(e.getGuild().getId(), textChannel.getId());
                        EmbedBuilder embedBuilder = new EmbedBuilder()
                                .setTitle("成功しましたっ！ (登録しました)")
                                .setDescription("おめでとうございます、これで地震情報を送信できます！")
                                .setColor(Color.GREEN)
                                .addField("チャンネル [ NEW ]", channel + " (<#" + channel + ">)", false)
                                .addField("発言可能か？", textChannel.canTalk() ? "はい" : "いいえ", false);

                        e.getTextChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                    } else {
                        SQLite.INSTANCE.getEarthQuake().update(e.getGuild().getId(), textChannel.getId());

                        EmbedBuilder embedBuilder = new EmbedBuilder()
                                .setTitle("成功しましたっ！ (アップデートしました)")
                                .setDescription("送信するチャンネルを変更しました！")
                                .setColor(Color.GREEN)
                                .addField("チャンネル [ UPDATE ]", channel + " (<#" + channel + ">)", false)
                                .addField("発言可能か？", textChannel.canTalk() ? "はい" : "いいえ", false);

                        e.getTextChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                    }
                }
            } else {

                String[] earthQuake = SQLite.INSTANCE.getEarthQuake().get(e.getGuild().getId());

                if (earthQuake == null) {
                    e.getMessage().reply("まだ登録されていないようですっ！").queue();
                } else {
                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setTitle("削除完了！・・・")
                            .setDescription("情報を削除しました・・")
                            .setColor(Color.GREEN)
                            .addField("チャンネル [ REMOVE ]", earthQuake[1] + " (<#" + earthQuake[1] + ">)", false);

                    SQLite.INSTANCE.getEarthQuake().delete(e.getGuild().getId());

                    e.getTextChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                }
            }
        } else {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("エラーが発生しましたっ！")
                    .setColor(Color.PINK)
                    .setDescription("本当にチャンネルを指定していますか？\n次の画像通りにしてみるか\nBOTにすべての権限を付与してみてください！")
                    .setImage("https://i.imgur.com/0jKYMk8.png");
            e.getTextChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }
}
