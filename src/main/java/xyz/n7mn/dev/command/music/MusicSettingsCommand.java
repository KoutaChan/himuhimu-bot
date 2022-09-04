package xyz.n7mn.dev.command.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;
import xyz.n7mn.dev.sqlite.data.SQLiteMusicData;
import xyz.n7mn.dev.util.MusicUtil;

import java.awt.*;

@CommandName(command = {"h.musicSettings", "h.ms"}, channelType = ChannelType.TEXT, commandType = CommandType.MUSIC, help = "BGMの設定を表示します")
public class MusicSettingsCommand extends Command {
    @Override
    public void CommandEvent(DiscordData e) {


        final SQLiteMusicData musicData = MusicUtil.getMusicData(e.getGuild());

        final EmbedBuilder embedBuilder = new EmbedBuilder().setTitle(e.getGuild().getName() + "での設定 (ミュージック)")
                .setColor(Color.YELLOW)
                .setDescription("これらの設定値は0～200です")
                .addField("初期音量", String.valueOf(musicData.getDefaultVolume()), false)
                .addField("最大音量", String.valueOf(musicData.getMaxVolume()), false);

        if (musicData.getDefaultVolume() > musicData.getMaxVolume()) {
            embedBuilder.addField("注意事項", "最大音量より初期音量のほうが大きいですが\n初期音量は最大音量の`" + musicData.getMaxVolume() + "`になります", false);
        }

        e.getMessage().replyEmbeds(embedBuilder.build()).queue();
    }
}