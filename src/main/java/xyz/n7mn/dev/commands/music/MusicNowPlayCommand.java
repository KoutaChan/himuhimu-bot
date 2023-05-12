package xyz.n7mn.dev.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.n7mn.dev.managers.slash.SlashCommand;
import xyz.n7mn.dev.managers.slash.SlashCommandListener;
import xyz.n7mn.dev.managers.slash.SubCommandType;
import xyz.n7mn.dev.util.CommonUtils;
import xyz.n7mn.dev.voice.AudioData;
import xyz.n7mn.dev.voice.AudioType;
import xyz.n7mn.dev.voice.music.MusicScheduler;

public class MusicNowPlayCommand extends SlashCommandListener {
    @SlashCommand(name = "nowplay", description = "現在再生されているBGMの詳細を表示します", commandType = SubCommandType.MUSIC)
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        AudioData data = getAudioData(event.getGuild());
        if (data == null) {
            event.replyEmbeds(getErrorEmbeds("現在ミュージックを流せる状態ではありません！ (理由: 現在ミュージックが流れていません)").build())
                    .setEphemeral(true)
                    .queue();
            return;
        }
        if (data.getType() != AudioType.MUSIC) {
            event.replyEmbeds(getErrorEmbeds("現在ミュージックを流せる状態ではありません！ (STATE=" + data.getType().name() + ")").build())
                    .setEphemeral(true)
                    .queue();
            return;
        }
        if (!data.getAudioChannel().equals(getConnectedVoiceChannel(event.getMember()))) {
            event.replyEmbeds(getErrorEmbeds("ひむひむちゃんBotと同じボイスチャンネルに参加してください").build())
                    .setEphemeral(true)
                    .queue();
            return;
        }
        AudioTrack track = data.getPlayer().getPlayingTrack();
        if (track == null) {
            event.replyEmbeds(getErrorEmbeds("現在ミュージックが流れていないようです！").build())
                    .setEphemeral(true)
                    .queue();
            return;
        }
        final double percentage = ((double) track.getPosition() / 1000) / ((double) track.getDuration() / 1000);
        event.replyEmbeds(new EmbedBuilder()
                        .setTitle(track.getInfo().title)
                        .setDescription("現在再生中の曲の情報はこちらです！")
                        .addField("音量", "```" + data.getVolume() + "%```", false)
                        .addField("リピート？", "```" + (((MusicScheduler) data.getListener()).isRepeat() ? "はい" : "いいえ") + "```", false)
                        .addField("プレイ時間", "```[" + track.getPosition() / 1000 + " / " + (track.getInfo().isStream ? "ライブ配信" : track.getDuration() / 1000) + "]```", false)
                        .addField("プレイ時間(プログレスバー)", "```[" + CommonUtils.getProgressBar(Math.round(30 * percentage), 30) + "]" + Math.round(percentage * 100) + "%```", false)
                        .build())
                .queue();
    }
}