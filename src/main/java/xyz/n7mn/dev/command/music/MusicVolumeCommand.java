package xyz.n7mn.dev.command.music;

import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.music.MusicManager;
import xyz.n7mn.dev.commandprocessor.*;
import xyz.n7mn.dev.sqlite.data.SQLiteMusicData;
import xyz.n7mn.dev.util.MusicUtil;

@CommandName(command = {"h.volume", "h.musicVolume"}, channelType = ChannelType.TEXT, commandType = CommandType.MUSIC, help = "BGMの音量を設定します\n%command% <0～100>でBGMの音量を設定します")
public class MusicVolumeCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        if (e.checkMusic(true)) {
            if (e.getArgs().length > 1) {

                SQLiteMusicData musicData = MusicUtil.getMusicData(e.getGuild());
                int MAX_VALUE = musicData.getMaxVolume();

                try {
                    final int integer = Integer.parseInt(e.getArgs()[1]);

                    final boolean isSuccess = MusicManager.getMusicManager().getMusicData(e.getGuild()).setVolume(musicData, integer);

                    e.getMessage().reply("音量を" + (isSuccess ? integer : MAX_VALUE) + "に変更しました！" + (isSuccess ? " (成功しました)" : "\n[!] 上限を超えたため設定されている上限まで設定しました")).queue();

                } catch (NumberFormatException ignore) {
                    e.getMessage().reply(String.format("えらーですっ\n`h.musicVolume <0～%s>`または`h.volume <0～%s>`でお願いしますっ！！", MAX_VALUE, MAX_VALUE)).queue();
                }
            } else {
                int volume = MusicManager.getMusicManager().getMusicData(e.getGuild()).getAudioPlayer().getVolume();

                e.getMessage().reply("現在の音量は " + volume + "ですっ！").queue();
            }
        }
    }
}