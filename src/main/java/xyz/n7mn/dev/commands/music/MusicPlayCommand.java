package xyz.n7mn.dev.commands.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import xyz.n7mn.dev.managers.search.button.UseButton;
import xyz.n7mn.dev.managers.search.button.ButtonInteract;
import xyz.n7mn.dev.managers.slash.Option;
import xyz.n7mn.dev.managers.slash.SlashCommand;
import xyz.n7mn.dev.managers.slash.SlashCommandListener;
import xyz.n7mn.dev.managers.slash.SubCommandType;
import xyz.n7mn.dev.voice.AudioData;
import xyz.n7mn.dev.voice.AudioManager;
import xyz.n7mn.dev.voice.AudioType;
import xyz.n7mn.dev.voice.music.MusicScheduler;

import java.util.Date;

@UseButton
public class MusicPlayCommand extends SlashCommandListener {
    @SlashCommand(name = "play", description = "ミュージックを再生します", options = @Option(type = OptionType.STRING, name = "video", description = "URLまたはYoutubeで検索したいタイトル"), commandType = SubCommandType.MUSIC)
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        AudioData data = getAudioData(event.getGuild());
        if (data == null) {
            AudioChannelUnion channelUnion = getConnectedVoiceChannel(event.getMember());
            if (channelUnion == null) {
                event.replyEmbeds(getErrorEmbeds("ボイスチャンネルに接続してください").build()).queue();
                return;
            }
            data = AudioManager.createAudio(AudioType.MUSIC, event.getGuildChannel(), channelUnion);
            event.getMessageChannel().sendMessageEmbeds(new EmbedBuilder()
                            .setTitle("VC に接続しました！")
                            .addField("接続先VC", channelUnion.getAsMention(), false)
                            .setTimestamp(new Date().toInstant())
                            .build())
                    .queue();
        } else if (!data.getAudioChannel().equals(getConnectedVoiceChannel(event.getMember()))) {
            event.replyEmbeds(getErrorEmbeds("ひむひむちゃんBotと同じボイスチャンネルに参加してください").build()).queue();
            return;
        }
        if (data.getType() == AudioType.MUSIC) {
            event.deferReply().queue();
            ((MusicScheduler) data.getListener()).search(event, event.getOption("video", OptionMapping::getAsString));
        } else {
            event.replyEmbeds(getErrorEmbeds("現在ミュージックを流せる状態ではありません！ (STATE=" + data.getType().name() + ")").build()).queue();
        }
    }

    @ButtonInteract(regex = "music-id-[0-4]")
    public void onYoutubeSearchInteraction(ButtonInteractionEvent event) {
        AudioData data = getAudioData(event.getGuild());
        if (data == null) {
            event.reply("ひむひむちゃんBotはVCに参加していないようです").setEphemeral(true).queue();
            return;
        }
        if (data.getType() != AudioType.MUSIC) {
            event.replyEmbeds(getErrorEmbeds("現在ミュージックを流せる状態ではありません！ (STATE=" + data.getType().name() + ")").build()).queue();
            return;
        }
        MessageEmbed.Field field = event.getMessage().getEmbeds().get(0).getFields().get(Integer.parseInt(event.getComponentId().replaceAll("[^0-4]", "")));
        ((MusicScheduler) data.getListener()).searchAndPlay(event, field.getValue());
    }
}