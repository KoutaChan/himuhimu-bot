package xyz.n7mn.dev.command.music;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.music.AudioTrackData;
import xyz.n7mn.dev.music.MusicManager;
import xyz.n7mn.dev.music.custom.BiliBiliVideo;
import xyz.n7mn.dev.music.custom.NicoVideo;
import xyz.n7mn.dev.commandprocessor.*;
import xyz.n7mn.dev.util.MusicUtil;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @credit - https://www.youtube.com/watch?v=wdBP3twRjl8 (よくわからないのでこれを使用)
 * v1.3 - recoded
 */

@CommandName(command = {"h.play"}, channelType = ChannelType.TEXT, commandType = CommandType.MUSIC, help = "BGMを再生します\nh.play <URL> で再生します")
public class MusicPlayCommand extends Command {

    // Regex="(bilibili.*)\/(.*?((?=[&#?])|$))"
    private static final Pattern bilibiliMatcher = Pattern.compile("(bilibili.*)/(.*?((?=[&#?])|$))");

    @Override
    public void CommandEvent(DiscordData e) {
        if (e.getArgs().length > 1) {

            if (e.getVoiceChoice(VoiceEnum.NONE)) {

                AudioChannel audioChannel = e.getGuild().getMember(e.getUser()).getVoiceState().getChannel();

                if (audioChannel == null) {
                    e.getTextChannel().sendMessage("ボイスチャットに入ってください！").queue();
                } else {
                    e.getGuild().getAudioManager().openAudioConnection(audioChannel);
                    MusicManager.getMusicManager().createMusicData(e.getGuild());

                    try {
                        int volume = Integer.parseInt(e.getArgs()[2]);

                        MusicManager.getMusicManager().getMusicData(e.getGuild()).setVolume(MusicUtil.getMusicData(e.getGuild()), volume);
                    } catch (Exception ignored) {
                        //ignored
                    }

                    run(e);
                }
            } else if (e.checkMusic(true)) {
                run(e);
            }
        } else {
            e.getTextChannel().sendMessage("URLまたは動画名を入力してくださいっ！").queue();
        }
    }

    public void run(DiscordData data) {
        new Thread(() -> {
            String url = Arrays.stream(data.getArgs())
                    .filter(r -> r != data.getArgs()[0])
                    .collect(Collectors.joining());

            if (url.contains("nicovideo.jp/watch/") || url.contains("nico.ms")) {
                MusicManager.getMusicManager().loadAndPlay(data.getTextChannel(), new AudioTrackData(null, new NicoVideo(url), url), true);
            } else {
                Matcher matcher = bilibiliMatcher.matcher(url);

                if (matcher.find()) {
                    MusicManager.getMusicManager().loadAndPlay(data.getTextChannel(), new AudioTrackData(null, new BiliBiliVideo(matcher.group(2)), url), true);
                } else {
                    MusicManager.getMusicManager().loadAndPlay(data.getTextChannel(), new AudioTrackData(null, url), true);
                }
            }
        }).start();
    }
}