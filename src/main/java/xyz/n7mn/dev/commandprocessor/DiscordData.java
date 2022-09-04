package xyz.n7mn.dev.commandprocessor;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.*;
import xyz.n7mn.dev.music.MusicManager;
import xyz.n7mn.dev.command.other.VoiceRoidStartCommand;
import xyz.n7mn.dev.sqlite.data.CasinoData;
import xyz.n7mn.dev.util.CasinoManager;
import xyz.n7mn.dev.util.DiscordUtil;
import xyz.n7mn.dev.yomiage.voice.VoiceManager;

@Getter @Setter
public class DiscordData {
    private Guild guild;
    private TextChannel textChannel;
    private Member member;
    private User user;
    private Message message;
    private JDA jda;
    private String messageText;
    private String[] args;

    public DiscordData(TextChannel textChannel, Message message, String[] args) {
        this.guild = textChannel.getGuild();
        this.textChannel = textChannel;
        this.member = message.getMember();
        this.user = message.getAuthor();
        this.message = message;
        this.messageText = message.getContentRaw();
        this.jda = message.getJDA();
        this.args = args != null ? args : message.getContentRaw().split("\\s+");
    }

    public boolean isAudioConnected() {
        return guild.getAudioManager().getConnectionStatus() == ConnectionStatus.CONNECTING_AWAITING_READY
                || guild.getAudioManager().getConnectionStatus() == ConnectionStatus.CONNECTING_ATTEMPTING_UDP_DISCOVERY
                || guild.getAudioManager().getConnectionStatus() == ConnectionStatus.CONNECTING_AWAITING_WEBSOCKET_CONNECT
                || guild.getAudioManager().getConnectionStatus() == ConnectionStatus.CONNECTING_AWAITING_AUTHENTICATION
                || guild.getAudioManager().getConnectionStatus() == ConnectionStatus.CONNECTING_AWAITING_ENDPOINT
                || guild.getAudioManager().getConnectionStatus() == ConnectionStatus.CONNECTED;
    }

    public boolean isSameAudio() {
        return getGuild().getMember(getUser()).getVoiceState().getChannel() == getGuild().getAudioManager().getConnectedChannel();
    }

    public VoiceEnum getVoiceEnum() {
        if (MusicManager.getMusicManager().hasMusicData(guild)) {
            return VoiceEnum.MUSIC;
        } else if (VoiceManager.getVoiceManager().hasVoiceData(guild)) {
            return VoiceEnum.VOICE_ROID;
        } else {
            return VoiceEnum.NONE;
        }
    }

    public boolean getVoiceChoice(VoiceEnum voiceEnum) {
        return getVoiceEnum() == voiceEnum;
    }

    public CasinoData getCasinoData() {
        return CasinoManager.getCasinoData(guild.getId(), user.getId());
    }

    public boolean checkMusic(boolean sendMessage) {
        if (isAudioConnected()) {
            if (isSameAudio()) {
                if (getVoiceChoice(VoiceEnum.MUSIC)) {
                    return true;
                } else {
                    if (sendMessage) getTextChannel().sendMessage("現在再生できる状態ではないようですっ！").queue();
                    return false;
                }
            } else {
                if (sendMessage) getTextChannel().sendMessage("あなたはBOTと同じボイスチャットに入っていないようですっ！").queue();
                return false;
            }
        } else {
            MusicManager.musicData.remove(guild.getIdLong());
            VoiceManager.voiceData.remove(guild.getIdLong());
            VoiceRoidStartCommand.textChannelId.remove(guild.getIdLong());

            if (sendMessage) getTextChannel().sendMessage("BOTはボイスチャンネルに入っていないようですっ！").queue();
            return false;
        }
    }

    public boolean checkCasino(boolean sendMessage) {
        if (getArgs().length > 1) {
            try {
                long num = Long.parseLong(getArgs()[1]);

                CasinoData casinoData = getCasinoData();
                if (casinoData.hasCoin(num)) {
                    return true;
                } else {
                    if (sendMessage) getMessage().reply("コインが足りませんよ！").queue();
                    return false;
                }
            } catch (NumberFormatException ex) {
                if (sendMessage) getMessage().reply("数字以外を入力しないでください！ 数字の上限は" + Long.MAX_VALUE + "です！").queue();
                return false;
            }
        } else {
            if (sendMessage) getMessage().reply("掛け金を入力してください！").queue();
            return false;
        }
    }


    public String translatePermission(Permission[] permissions) {

        if (permissions[0] == Permission.UNKNOWN) return "必要なし";

        StringBuilder stringBuilder = new StringBuilder();

        for (Permission permission : permissions) {
            addField(stringBuilder, DiscordUtil.translatePermission(permission));
        }

        return stringBuilder.toString();
    }

    private void addField(StringBuilder stringBuilder, String string) {
        if (stringBuilder.length() == 0) stringBuilder.append(string);
        else stringBuilder.append("\n").append(string);
    }
}