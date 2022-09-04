package xyz.n7mn.dev.command.other;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.commandprocessor.*;
import xyz.n7mn.dev.yomiage.data.TextChannelData;
import xyz.n7mn.dev.yomiage.voice.VoiceManager;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@CommandName(command = {"h.yostart"}, channelType = ChannelType.TEXT, commandType = CommandType.OTHER, help = "discordのチャットを読んでくれます\n:warning: ボイスチャンネルに入っている必要があります\n:warning: 正常に動かない場合があります", maintenance = false, maintenanceMessage = "現在稼働できません・・・")
public class VoiceRoidStartCommand extends Command {

    public static Map<Long, TextChannelData> textChannelId = new HashMap<>();

    @Override
    public void CommandEvent(DiscordData e) {
        if (!e.isAudioConnected()) {
            if (e.getVoiceChoice(VoiceEnum.NONE)) {
                AudioChannel audioChannel = e.getGuild().getMember(e.getUser()).getVoiceState().getChannel();

                if (audioChannel == null) {
                    e.getTextChannel().sendMessage("ボイスチャットに入ってください！").queue();
                } else {
                    e.getGuild().getAudioManager().openAudioConnection(audioChannel);
                    textChannelId.put(e.getGuild().getIdLong(), new TextChannelData(e.getTextChannel()));

                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setColor(Color.YELLOW)
                            .setTitle("接続完了しました")
                            .addField("接続チャンネル", "<#" + audioChannel.getId() + ">", false)
                            .addField("テキストチャンネル", "<#" + e.getTextChannel().getId() + ">", false);

                    VoiceManager.getVoiceManager().createVoiceData(e.getGuild());
                    e.getTextChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                }
            } else {
                e.getTextChannel().sendMessage("現在起動できる状態ではないようですっ！").queue();
            }
        }
    }
}