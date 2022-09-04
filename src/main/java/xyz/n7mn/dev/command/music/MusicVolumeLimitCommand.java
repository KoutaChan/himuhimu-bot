package xyz.n7mn.dev.command.music;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.music.MusicData;
import xyz.n7mn.dev.music.MusicManager;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;
import xyz.n7mn.dev.util.MusicUtil;

@CommandName(command = {"h.volumeLimit", "h.musicVolumeLimit"}, channelType = ChannelType.TEXT, commandType = CommandType.MUSIC, help = "BGMの音量の上限を設定します\nはじめの初期音量は通常は`100`です", permission = {Permission.ADMINISTRATOR, Permission.VOICE_MUTE_OTHERS})
public class MusicVolumeLimitCommand extends Command {
    @Override
    public void CommandEvent(DiscordData e) {
        if (e.getArgs().length > 1) {
            try {
                final int integer = Integer.parseInt(e.getArgs()[1]);

                if (integer <= 200 && integer >= 0) {
                    MusicUtil.getMusicData(e.getGuild()).setMaxVolume(integer);

                    if (MusicManager.getMusicManager().hasMusicData(e.getGuild())) {
                        MusicData musicData = MusicManager.getMusicManager().getMusicData(e.getGuild());
                        if (musicData.getAudioPlayer().getVolume() > integer) {
                            musicData.getAudioPlayer().setVolume(integer);
                        }
                    }

                    e.getMessage().reply("音量の上限を" + integer + "に変更しました！").queue();

                } else {
                    e.getMessage().reply("えらーですっ\n`h.volumeLimit <0～200>`または`h.musicVolumeLimit <0～200>`でお願いしますっ！！").queue();
                }

            } catch (NumberFormatException ignore) {
                e.getMessage().reply("えらーですっ\n`h.volumeLimit <0～200>`または`h.musicVolumeLimit <0～200>`でお願いしますっ！！").queue();
            }
        } else {
            int volume = MusicUtil.getMusicData(e.getGuild()).getMaxVolume();

            e.getMessage().reply("現在の音量の上限は " + volume + "ですっ！").queue();
        }
    }
}
