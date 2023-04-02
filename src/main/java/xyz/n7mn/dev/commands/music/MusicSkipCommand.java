package xyz.n7mn.dev.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import xyz.n7mn.dev.managers.slash.Option;
import xyz.n7mn.dev.managers.slash.SlashCommand;
import xyz.n7mn.dev.managers.slash.SlashCommandListener;
import xyz.n7mn.dev.managers.slash.SubCommandType;
import xyz.n7mn.dev.util.TimeUtils;
import xyz.n7mn.dev.voice.AudioData;
import xyz.n7mn.dev.voice.AudioType;

import java.awt.*;
import java.util.List;

public class MusicSkipCommand extends SlashCommandListener {
    @SlashCommand(name = "skip", description = "ミュージックをスキップします", options = @Option(type = OptionType.INTEGER, name = "size", description = "スキップする数", required = false), commandType = SubCommandType.MUSIC)
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
        final int size = event.getOption("size", 1, OptionMapping::getAsInt);
        if (size <= 0) {
            event.replyEmbeds(getErrorEmbeds("サイズがおかしいです、1以上を選択してください").build()).queue();
            return;
        }
        List<AudioTrack> skippedAudioTrack = data.getListener().skip(event.getOption("size", 1, OptionMapping::getAsInt));
        EmbedBuilder sentSkippedLists = new EmbedBuilder()
                .setColor(skippedAudioTrack.size() == size ? Color.GREEN : Color.RED)
                .setTitle(skippedAudioTrack.size() + "件スキップしました");
        StringBuilder as = new StringBuilder();
        for (int i = 1; i <= Math.min(skippedAudioTrack.size(), 5); i++) {
            if (!as.isEmpty()) {
                as.append("\n");
            }
            AudioTrack skipped = skippedAudioTrack.get(i - 1);
            as.append(i).append(": ").append(skipped.getInfo().title).append(" (").append(TimeUtils.convertMillisecondsToFormat(skipped.getDuration())).append(")");
        }
        event.replyEmbeds(sentSkippedLists.addField("スキップした動画", as.toString(), false).build()).queue();
    }
}
