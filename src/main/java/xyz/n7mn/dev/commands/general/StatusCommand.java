package xyz.n7mn.dev.commands.general;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import xyz.n7mn.dev.HimuHimuMain;
import xyz.n7mn.dev.managers.slash.SlashCommand;
import xyz.n7mn.dev.managers.slash.SlashCommandListener;
import xyz.n7mn.dev.managers.slash.SlashCommandManager;

import java.awt.*;

public class StatusCommand extends SlashCommandListener {
    @SlashCommand(name = "status", description = "Botのステータス情報")
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        event.replyEmbeds(new EmbedBuilder()
                        .setTitle("ひむひむちゃんBOT ステータス情報")
                        .setColor(Color.GREEN)
                        .addField("バージョン ", "2.0.0-PRE", false)
                        .addField("コマンド数 (サブコマンド含む) ", SlashCommandManager.getListeners().size() + "個", false)
                        .addField("サーバー導入数", HimuHimuMain.getJDA().getGuilds().size() + "個のサーバーで導入されています！", false)
                        .build())
                .addActionRow(Button.of(ButtonStyle.LINK, "https://github.com/KoutaChan/himuhimu-bot", "ヒムヒムちゃんBotのソースコード"))
                .queue();
    }
}