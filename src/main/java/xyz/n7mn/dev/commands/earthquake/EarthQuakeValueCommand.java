package xyz.n7mn.dev.commands.earthquake;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import xyz.n7mn.dev.managers.slash.*;
import xyz.n7mn.dev.managers.slash.Choice;
import xyz.n7mn.dev.sqlite.EarthQuakeDB;
import xyz.n7mn.dev.sqlite.SQLite;

import java.awt.*;

public class EarthQuakeValueCommand extends SlashCommandListener {
    @SlashCommand(name = "value", description = "地震情報の設定", options = {@Option(type = OptionType.STRING, name = "type", description = "設定する内容", choices = {@Choice(name = "リアルタイム地震情報", value = "realtime_earthquake_info"), @Choice(name = "地震情報", value = "earthquake_info")}), @Option(type = OptionType.STRING, name = "value", description = "設定", choices = {@Choice(name = "オン", value = "on"), @Choice(name = "オフ", value = "off")}, required = false)}, commandType = SubCommandType.SETTINGS_EARTHQUAKE)
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        EarthQuakeDB.EarthQuakeData data = SQLite.INSTANCE.getEarthQuake().get(event.getGuild().getId());
        if (data == null) {
            event.reply("地震情報の通知を受け取る設定をしていないため削除できません！").setEphemeral(true).queue();
            return;
        }
        String type = event.getOption("type", OptionMapping::getAsString);
        String value = event.getOption("value", OptionMapping::getAsString);
        Boolean valueBoolean = value == null ? null : value.equalsIgnoreCase("on");
        if (type.equalsIgnoreCase("realtime_earthquake_info")) {
            data.setAnnounceRealTime(valueBoolean == null ? !data.isAnnounceRealTime(): valueBoolean);
        } else if (type.equalsIgnoreCase("earthquake_info")) {
            data.setAnnounceInfo(valueBoolean == null ? !data.isAnnounceInfo() : valueBoolean);
        } else {
            event.getChannel().sendMessage("Unknown Error!").queue();
        }
        data.update();
        event.replyEmbeds(createEmbeds(data)).queue();
    }

    public MessageEmbed createEmbeds(EarthQuakeDB.EarthQuakeData data) {
        return new EmbedBuilder()
                .setTitle("アナウンス設定")
                .setColor(Color.GREEN)
                .addField("リアルタイム地震", data.isAnnounceRealTime() ? "オン" : "オフ", false)
                .addField("地震情報", data.isAnnounceInfo() ? "オン" : "オフ", false)
                .build();
    }
}