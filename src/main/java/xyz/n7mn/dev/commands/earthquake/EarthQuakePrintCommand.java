package xyz.n7mn.dev.commands.earthquake;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import xyz.n7mn.dev.HimuHimuMain;
import xyz.n7mn.dev.managers.slash.SlashCommand;
import xyz.n7mn.dev.managers.slash.SlashCommandListener;
import xyz.n7mn.dev.managers.slash.SubCommandType;
import xyz.n7mn.dev.util.CommonUtils;

import java.text.DateFormat;
import java.util.Date;

public class EarthQuakePrintCommand extends SlashCommandListener {
    @SlashCommand(name = "print", description = "リアルタイムの地震情報をプリントします", commandType = SubCommandType.EARTHQUAKE)
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        event.reply("しばらくお待ち下さい、現在生成中です").queue(message -> HimuHimuMain.getEarthQuakeYahooMonitor().getQueue().add(image -> {
            message.editOriginal("").queue();
            message.editOriginalEmbeds(new EmbedBuilder()
                            .setTitle("Realtime Sindo")
                            .setDescription("大きな誤差・誤検知を含んでいる可能性があります")
                            .setFooter(DateFormat.getDateTimeInstance().format(new Date()) + "に作成されました | 防災科研（NIED）| Yahoo")
                            .setImage("attachment://earthquake.png").build())
                    .setFiles(FileUpload.fromData(CommonUtils.toByteArray(image), "earthquake.png"))
                    .queue();
        }));
    }
}