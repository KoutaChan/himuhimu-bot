package xyz.n7mn.dev.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.commandprocessor.*;

import java.awt.*;

@CommandName(command = {"h.helpdm"}, channelType = ChannelType.TEXT, commandType = CommandType.GENERAL, help = "ヘルプをDMに送ります！")
public class HelpDMCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        StringBuilder GENERAL = new StringBuilder();
        StringBuilder FUN = new StringBuilder();
        StringBuilder CASINO = new StringBuilder();
        StringBuilder MUSIC = new StringBuilder();
        StringBuilder EARTHQUAKE = new StringBuilder();
        StringBuilder OTHER = new StringBuilder();

        for (Class<? extends Command> command : CommandManager.command) {

            CommandName annotation = command.getAnnotation(CommandName.class);

            try {

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("\n`").append(annotation.command()[0]).append("`:");
                stringBuilder.append("\n説明 》 ").append(annotation.help().replaceAll("%command%", annotation.command()[0]));
                for (int i = 1; i < annotation.command().length; i++) {
                    stringBuilder.append("\n" + "サブコマンド 》 ").append(annotation.command()[i]);
                }

                try {
                    switch (annotation.commandType()) {
                        case GENERAL:
                            GENERAL.append(stringBuilder);
                            break;
                        case FUN:
                            FUN.append(stringBuilder);
                            break;
                        case CASINO:
                            CASINO.append(stringBuilder);
                            break;
                        case MUSIC:
                            MUSIC.append(stringBuilder);
                            break;
                        case EARTHQUAKE:
                            EARTHQUAKE.append(stringBuilder);
                            break;
                        case OTHER:
                            OTHER.append(stringBuilder);
                            break;
                    }
                } catch (Exception ignore) {

                }
            } catch (Exception ignore) {

            }
        }

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("ひむひむちゃんBOTの現在のコマンドリスト")
                .setDescription(e.getMessage().getTimeCreated() + "時点でのコマンドリスト\nh.help <カテゴリー> でカテゴリ別のコマンドを表示可能です！")
                .setColor(Color.YELLOW)
                .addField(":tools: General", GENERAL.toString(), false)
                .addField(":roller_coaster: Fun", FUN.toString(), false)
                .addField(":coin: Casino", CASINO.toString(), false)
                .addField(":musical_note: Music", ":warning: ボイスチャンネルに入っている必要があります\n:exclamation: ベータ: 検索機能に対応しました" + MUSIC, false)
                .addField(":earth_asia: EarthQuake", ":warning: 気象庁の情報に基づいて送られています" + EARTHQUAKE, false)
                .addField(":question: Other", OTHER.toString(), false);


        e.getUser().openPrivateChannel().complete().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}