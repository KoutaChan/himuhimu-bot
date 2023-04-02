package xyz.n7mn.dev.commands.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import xyz.n7mn.dev.managers.button.UseButton;
import xyz.n7mn.dev.managers.button.ButtonInteract;
import xyz.n7mn.dev.managers.slash.Option;
import xyz.n7mn.dev.managers.slash.SlashCommand;
import xyz.n7mn.dev.managers.slash.SlashCommandListener;
import xyz.n7mn.dev.managers.slash.SubCommandType;
import xyz.n7mn.dev.voice.AudioData;
import xyz.n7mn.dev.voice.AudioManager;
import xyz.n7mn.dev.voice.AudioType;

import java.awt.*;

@UseButton
public class MusicVolumeCommand extends SlashCommandListener {
    @SlashCommand(name = "volume", description = "ミュージックのボリュームを設定します", options = @Option(type = OptionType.INTEGER, name = "volume", description = "新しいボリューム", required = false), commandType = SubCommandType.MUSIC)
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        AudioData data = getAudioData(event.getGuild());
        if (data == null) {
            AudioChannelUnion channelUnion = getConnectedVoiceChannel(event.getMember());
            if (channelUnion == null) {
                event.replyEmbeds(getErrorEmbeds("ボイスチャンネルに接続してください").build()).queue();
                return;
            }
            data = AudioManager.createAudio(AudioType.MUSIC, event.getGuildChannel(), channelUnion);
        } else if (!data.getAudioChannel().equals(getConnectedVoiceChannel(event.getMember()))) {
            event.replyEmbeds(getErrorEmbeds("ひむひむちゃんBotと同じボイスチャンネルに参加してください").build()).queue();
            return;
        }
        if (data.getType() == AudioType.MUSIC) {
            OptionMapping volume = event.getOption("volume");
            if (volume != null) {
                data.setVolume(volume.getAsInt());
            }
            event.replyEmbeds(createVolumeEmbeds(data).build())
                    .setActionRow(Button.of(ButtonStyle.DANGER, "volume-decrease-10", "-10%", Emoji.fromUnicode("U+23EE")),
                            Button.of(ButtonStyle.DANGER, "volume-decrease-1", "-1%", Emoji.fromUnicode("U+23EA")),
                            Button.of(ButtonStyle.SUCCESS, "volume-increase-1", "+1%", Emoji.fromUnicode("U+23E9")),
                            Button.of(ButtonStyle.SUCCESS, "volume-increase-10", "+10%", Emoji.fromUnicode("U+23ED")))
                    .queue();
        } else {
            event.replyEmbeds(getErrorEmbeds("現在ミュージックのボリュームを変更できる状態ではありません！ (STATE=" + data.getType().name() + ")").build()).queue();
        }
    }

    @ButtonInteract(regex = "volume-(decrease|increase)-[0-9]*")
    public void onVolumeButtonInteraction(ButtonInteractionEvent event) {
        AudioData data = AudioManager.getAudio(event.getGuild());
        if (data == null) {
            event.reply("ひむひむちゃんBotはVCに参加していないようです").setEphemeral(true).queue();
        } else {
            final boolean isIncrease = event.getComponentId().startsWith("volume-increase");
            final boolean changed = data.setVolume(isIncrease
                    ? data.getVolume() + Integer.parseInt(event.getComponentId().replaceAll("[^0-9]", ""))
                    : data.getVolume() - Integer.parseInt(event.getComponentId().replaceAll("[^0-9]", "")));
            event.editMessageEmbeds(createVolumeEmbeds(new EmbedBuilder().addField("最後の使用者", event.getMember().getAsMention(), false), data, changed ? Color.GREEN : Color.RED).build()).queue();
        }
    }

    public EmbedBuilder createVolumeEmbeds(AudioData data) {
        return createVolumeEmbeds(data, Color.GREEN);
    }

    public EmbedBuilder createVolumeEmbeds(AudioData data, Color color) {
        return createVolumeEmbeds(new EmbedBuilder(), data, color);
    }

    public EmbedBuilder createVolumeEmbeds(EmbedBuilder embedBuilder, AudioData data, Color color) {
        return embedBuilder
                .setColor(color)
                .setTitle("ボリュームコントローラー")
                .setDescription("ボタンをクリックすることで変更可能です")
                .addField("現在のボリューム", data.getVolume() + "%", false)
                .addField("最大音量", data.getMaxVolume() + "%", false);
    }
}