package xyz.n7mn.dev.commands.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import xyz.n7mn.dev.managers.slash.Option;
import xyz.n7mn.dev.managers.slash.SlashCommand;
import xyz.n7mn.dev.managers.slash.SlashCommandListener;
import xyz.n7mn.dev.managers.slash.SubCommandType;
import xyz.n7mn.dev.voice.AudioData;
import xyz.n7mn.dev.voice.AudioType;
import xyz.n7mn.dev.voice.music.MusicScheduler;

import java.awt.*;

public class MusicRepeatCommand extends SlashCommandListener {
    @SlashCommand(name = "repeat", description = "ミュージックをリピートします", options = @Option(type = OptionType.BOOLEAN, name = "value", description = "リピートモードをオン/オフ可能", required = false), commandType = SubCommandType.MUSIC)
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        AudioData data = getAudioData(event.getGuild());
        if (data == null) {
            event.replyEmbeds(getErrorEmbeds("現在ミュージックを流せる状態ではありません！ (理由: 現在ミュージックが流れていません)").build()).queue();
            return;
        }
        if (data.getType() != AudioType.MUSIC) {
            event.replyEmbeds(getErrorEmbeds("現在ミュージックを流せる状態ではありません！ (STATE=" + data.getType().name() + ")").build()).queue();
            return;
        }
        if (!data.getAudioChannel().equals(getConnectedVoiceChannel(event.getMember()))) {
            event.replyEmbeds(getErrorEmbeds("ひむひむちゃんBotと同じボイスチャンネルに参加してください").build()).queue();
            return;
        }
        final boolean repeatStatus = ((MusicScheduler) data.getListener()).setRepeat(event.getOption("value", !((MusicScheduler) data.getListener()).isRepeat(), OptionMapping::getAsBoolean));
        event.replyEmbeds(new EmbedBuilder()
                        .setColor(Color.PINK)
                        .setTitle("リピートモードを変更しました")
                        .addField("リピート", repeatStatus ? "ON" : "OFF", false)
                        .build())
                .queue();
    }
}