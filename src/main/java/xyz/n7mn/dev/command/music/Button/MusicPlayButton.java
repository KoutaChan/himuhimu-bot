package xyz.n7mn.dev.command.music.Button;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import xyz.n7mn.dev.buttonprocessor.Button;
import xyz.n7mn.dev.buttonprocessor.ButtonName;
import xyz.n7mn.dev.music.AudioTrackData;
import xyz.n7mn.dev.music.MusicManager;
import xyz.n7mn.dev.commandprocessor.VoiceEnum;
import xyz.n7mn.dev.util.DiscordUtil;

import java.util.ArrayList;
import java.util.List;

//max is 10
@ButtonName(componentId = {"music-1", "music-2", "music-3", "music-4", "music-5"})
public class MusicPlayButton extends Button {
    @Override
    public void ButtonEvent(ButtonInteractionEvent e) {
        if (DiscordUtil.getVoiceChoice(VoiceEnum.MUSIC, e.getGuild())) {
            final int count = Integer.parseInt(e.getComponentId().replaceAll("[^0-9]", ""));

            MessageEmbed messageEmbed = e.getMessage().getEmbeds().get(0);

            MessageEmbed.Field field = messageEmbed.getFields().get(count - 1);

            MusicManager.getMusicManager().loadAndPlay(e.getChannel().asTextChannel(), new AudioTrackData(null, field.getValue()), false);
        } else {
            e.getChannel().sendMessage(e.getMember().getAsMention() + "さん！ おかしいですね、BOTがミュージックを流せる状態ではないようです").queue();
        }

        final List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttonList = new ArrayList<>();
        e.getMessage().getButtons().forEach(button -> buttonList.add(button.asDisabled()));

        //インタラクションに失敗しましたをなくすため..... && ボタンをオフにするため
        e.editMessage(e.getMessage()).setActionRow(buttonList).queue();
    }
}