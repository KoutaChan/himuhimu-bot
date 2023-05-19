package xyz.n7mn.dev.commands.earthquake.settings;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import xyz.n7mn.dev.managers.search.button.UseButton;
import xyz.n7mn.dev.managers.search.button.ButtonInteract;
import xyz.n7mn.dev.managers.slash.SlashCommand;
import xyz.n7mn.dev.managers.slash.SlashCommandListener;
import xyz.n7mn.dev.managers.slash.SubCommandType;
import xyz.n7mn.dev.sqlite.earthquake.EarthQuakeConnection;
import xyz.n7mn.dev.sqlite.SQLite;

import java.awt.*;

@UseButton
public class EarthQuakeResetCommand extends SlashCommandListener {
    @SlashCommand(name = "reset", description = "地震情報の通知を解除します", commandType = SubCommandType.SETTINGS_EARTHQUAKE)
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        EarthQuakeConnection.EarthQuakeData data = SQLite.INSTANCE.getEarthQuake().get(event.getGuild().getId());
        if (data == null) {
            event.reply("地震情報の通知を受け取る設定をしていないため削除できません！").setEphemeral(true).queue();
            return;
        }
        event.replyEmbeds(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle("本当に削除しますか？")
                        .setDescription("本当に削除する場合は下のボタンを押してください")
                        .build())
                .setEphemeral(true)
                .setActionRow(Button.of(ButtonStyle.DANGER, "earthquake-reset", "削除する"))
                .queue();
    }

    @ButtonInteract(regex = "earthquake-reset")
    public void onEarthQuakeReset(ButtonInteractionEvent event) {
        EarthQuakeConnection.EarthQuakeData data = SQLite.INSTANCE.getEarthQuake().get(event.getGuild().getId());
        if (data == null) {
            event.reply("地震情報の通知を受け取る設定をしていないため削除できません！").setEphemeral(true).queue();
            return;
        }
        data.remove();
        event.editMessageEmbeds(event.getMessage().getEmbeds())
                .setActionRow(Button.of(ButtonStyle.DANGER, "earthquake-reset", "削除する").asDisabled())
                .queue();
        event.getMessageChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle("地震情報のお知らせをしなくなりました")
                        .setDescription("ひむひむちゃんBotは再設定しない限り地震情報をお知らせしてくれなくなりました")
                        .addField("実行したユーザー", event.getMember().getAsMention(), false)
                        .build())
                .queue();
    }
}