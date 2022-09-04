package xyz.n7mn.dev.command.music;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;
import xyz.n7mn.dev.util.MusicUtil;

@CommandName(command = {"h.defaultLimit"}, channelType = ChannelType.TEXT, commandType = CommandType.MUSIC, help = "BGMの初期音量を設定します\nはじめの初期音量は通常は`100`です", permission = {Permission.ADMINISTRATOR, Permission.VOICE_MUTE_OTHERS})
public class MusicVolumeDefaultCommand extends Command {
    @Override
    public void CommandEvent(DiscordData e) {
        if (e.getArgs().length > 1) {
            try {
                final int integer = Integer.parseInt(e.getArgs()[1]);

                if (integer <= 200 && integer >= 0) {
                    MusicUtil.getMusicData(e.getGuild()).setDefaultVolume(integer);

                    e.getMessage().reply("初期音量を" + integer + "に変更しました！").queue();

                } else {
                    e.getMessage().reply("えらーですっ\n`h.defaultLimit <0～200>`でお願いしますっ！！").queue();
                }

            } catch (NumberFormatException ignore) {
                e.getMessage().reply("えらーですっ\n`h.defaultLimit <0～200>`でお願いしますっ！！").queue();
            }
        } else {
            int volume = MusicUtil.getMusicData(e.getGuild()).getDefaultVolume();

            e.getMessage().reply("現在の初期音量は " + volume + "ですっ！").queue();
        }
    }
}
