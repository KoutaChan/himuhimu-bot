package xyz.n7mn.dev.commands.earthquake;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import xyz.n7mn.dev.managers.button.UseButton;
import xyz.n7mn.dev.managers.button.ButtonInteract;
import xyz.n7mn.dev.managers.slash.Option;
import xyz.n7mn.dev.managers.slash.SlashCommand;
import xyz.n7mn.dev.managers.slash.SlashCommandListener;
import xyz.n7mn.dev.managers.slash.SubCommandType;
import xyz.n7mn.dev.sqlite.EarthQuakeDB;
import xyz.n7mn.dev.sqlite.SQLite;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@UseButton
public class EarthQuakeRegisterCommand extends SlashCommandListener {
    private final Collection<ChannelType> ALLOWED_CHANNEL_TYPE = Arrays.asList(ChannelType.TEXT, ChannelType.NEWS, ChannelType.GUILD_NEWS_THREAD, ChannelType.GUILD_PRIVATE_THREAD, ChannelType.GUILD_PUBLIC_THREAD);

    @SlashCommand(name = "register", description = "地震情報の通知を登録します", options = @Option(name = "channel", description = "登録するチャンネル", type = OptionType.CHANNEL, required = false), commandType = SubCommandType.SETTINGS_EARTHQUAKE)
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        GuildChannelUnion channel = event.getOption("channel", OptionMapping::getAsChannel);
        if (channel == null) {
            event.replyEmbeds(new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setTitle("地震情報をお知らせするチャンネルを設定しますか？")
                            .setDescription("メニューで選択してください")
                            .addField("変更しようとしているユーザー", event.getMember().getAsMention(), false)
                            .build())
                    .setActionRow(EntitySelectMenu.create("choose-earthquake-channel", EntitySelectMenu.SelectTarget.CHANNEL).setChannelTypes(ALLOWED_CHANNEL_TYPE).build())
                    .queue();
            return;
        }
        EarthQuakeDB.EarthQuakeData data = SQLite.INSTANCE.getEarthQuake().get(event.getGuild().getId());
        if (!(channel instanceof StandardGuildMessageChannel standard)) {
            event.replyEmbeds(getErrorEmbeds("登録できないチャンネルタイプです").build()).queue();
            return;
        }
        if (data == null) {
            SQLite.INSTANCE.getEarthQuake().insert(event.getGuild().getId(), channel.getId());
            event.replyEmbeds(new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setTitle("地震情報をお知らせするチャンネルを登録しました！")
                            .addField("登録したユーザー", event.getMember().getAsMention(), false)
                            .addField("以下の内容で登録しました", channel.getAsMention(), false)
                            .addField("発言可能", (standard.canTalk() ? "はい" : "いいえ"), false)
                            .build())
                    .queue();
        } else {
            event.replyEmbeds(new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setTitle("地震情報をお知らせするチャンネルを変更しますか？")
                            .addField("変更しようとしているユーザー", event.getMember().getAsMention(), false)
                            .addField("変更先", channel.getAsMention(), false)
                            .build())
                    .setActionRow(Button.of(ButtonStyle.SUCCESS, "earthquake-override", "上書きする"))
                    .queue();
        }
    }

    @ButtonInteract(regex = "earthquake-override")
    public void onEarthQuakeOverride(ButtonInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.replyEmbeds(getErrorEmbeds("あなたは権限を持っていません (サーバー権限が必要です)").build()).setEphemeral(true).queue();
            return;
        }
        MessageEmbed embeds = event.getMessage().getEmbeds().get(0);
        Optional<MessageEmbed.Field> fieldOptional = embeds.getFields().stream().filter(d -> d != null && d.getName() != null && d.getName().equals("変更先")).findFirst();
        if (fieldOptional.isPresent()) {
            StandardGuildMessageChannel channel = event.getGuild().getChannelById(StandardGuildMessageChannel.class, getChannel(fieldOptional.get().getValue()));
            if (channel != null) {
                EarthQuakeDB.EarthQuakeData data = SQLite.INSTANCE.getEarthQuake().get(event.getGuild().getId());
                if (data == null) {
                    SQLite.INSTANCE.getEarthQuake().insert(event.getGuild().getId(), channel.getId());
                } else {
                    data.setChannel(channel.getId());
                    data.update();
                }
                event.replyEmbeds(new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setTitle(data == null ? "地震情報をお知らせするチャンネルを登録しました！" : "地震情報をお知らせするチャンネルを変更しました！")
                                .addField("変更したユーザー", event.getMember().getAsMention(), false)
                                .addField("以下の内容で登録しました", channel.getAsMention(), false)
                                .addField("発言可能", (channel.canTalk() ? "はい" : "いいえ"), false)
                                .build())
                        .setActionRow(Button.of(ButtonStyle.SUCCESS, "earthquake-override", "上書きする").asDisabled())
                        .queue();
            }
        }
        event.editMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle("チャンネルが存在しないようです")
                        .addField("変更したユーザー", event.getMember().getAsMention(), false)
                        .addField("変更先", "不明", false)
                        .build())
                .setActionRow(Button.of(ButtonStyle.SUCCESS, "earthquake-override", "上書きする").asDisabled())
                .queue();
    }
}