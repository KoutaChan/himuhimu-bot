package xyz.n7mn.dev.commands.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.n7mn.dev.managers.slash.SlashCommand;
import xyz.n7mn.dev.managers.slash.SlashCommandListener;
import xyz.n7mn.dev.managers.slash.SubCommandType;
import xyz.n7mn.dev.voice.AudioData;
import xyz.n7mn.dev.voice.AudioType;

import java.awt.*;
import java.util.Date;

public class MusicStopCommand extends SlashCommandListener {
    @SlashCommand(name = "stop", description = "ミュージックを停止します", commandType = SubCommandType.MUSIC)
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
        data.remove();
        //TODO: A
        AudioChannelUnion audioChannel = data.getAudioChannel();
        event.replyEmbeds(new EmbedBuilder()
                        .setTitle("[!] ミュージックを再生を停止しました")
                        .setColor(Color.PINK)
                        .addField("接続していたVC", audioChannel == null ? "N/A (Unknown VoiceChannel)" : audioChannel.getAsMention(), false)
                        .setTimestamp(new Date().toInstant())
                        .build())
                .queue();
    }
}
