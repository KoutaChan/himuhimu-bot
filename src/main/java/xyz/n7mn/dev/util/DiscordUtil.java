package xyz.n7mn.dev.util;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import xyz.n7mn.dev.command.other.VoiceRoidStartCommand;
import xyz.n7mn.dev.commandprocessor.VoiceEnum;
import xyz.n7mn.dev.music.AudioTrackData;
import xyz.n7mn.dev.music.MusicManager;
import xyz.n7mn.dev.yomiage.voice.VoiceManager;

@UtilityClass
public class DiscordUtil {
    public String translatePermission(Permission permission) {
        switch (permission) {
            case VIEW_CHANNEL:
                return "チャンネルを見る";
            case MANAGE_CHANNEL:
                return "チャンネルの管理";
            case MANAGE_ROLES:
                return "ロールの管理";
            case MANAGE_EMOJIS_AND_STICKERS:
                return "絵文字・スタンプの管理";
            case VIEW_AUDIT_LOGS:
                return "監査ログ";
            case MANAGE_WEBHOOKS:
                return "ウェブフックの管理";
            case MANAGE_SERVER:
                return "サーバーの管理";
            case CREATE_INSTANT_INVITE:
                return "招待を作成";
            case NICKNAME_CHANGE:
                return "ニックネームの変更";
            case NICKNAME_MANAGE:
                return "ニックネームの管理";
            case KICK_MEMBERS:
                return "メンバーのキック";
            case BAN_MEMBERS:
                return "メンバーをBAN";
            case MODERATE_MEMBERS:
                return "メンバーをタイムアウト";
            case MESSAGE_SEND:
                return "メッセージを送信";
            case MESSAGE_SEND_IN_THREADS:
                return "スレッドでメッセージを送信";
            case CREATE_PUBLIC_THREADS:
                return "公開スレッドを作成";
            case CREATE_PRIVATE_THREADS:
                return "プライベートスレッドの作成";
            case MESSAGE_EMBED_LINKS:
                return "埋め込みリンク";
            case MESSAGE_ATTACH_FILES:
                return "ファイルを添付";
            case MESSAGE_ADD_REACTION:
                return "リアクションの追加";
            case MESSAGE_EXT_EMOJI:
                return "外部の絵文字を使用する";
            case MESSAGE_EXT_STICKER:
                return "外部のスタンプを使用する";
            case MESSAGE_MENTION_EVERYONE:
                return "@everyone、@here、すべてのロールにメンション";
            case MESSAGE_MANAGE:
                return "メッセージの管理";
            case MANAGE_THREADS:
                return "スレッドの管理";
            case MESSAGE_HISTORY:
                return "メッセージ履歴を読む";
            case MESSAGE_TTS:
                return "テキスト読み上げメッセージを送信";
            case USE_APPLICATION_COMMANDS:
                return "アプリコマンドを使う";
            case VOICE_CONNECT:
                return "ボイスチャンネルに接続";
            case VOICE_SPEAK:
                return "ボイスチャンネルでの発言";
            case VOICE_STREAM:
                return "WEB カメラ";
            case VOICE_START_ACTIVITIES:
                return "アクティビティ";
            case VOICE_USE_VAD:
                return "音声検出";
            case PRIORITY_SPEAKER:
                return "優先スピーカー";
            case VOICE_MUTE_OTHERS:
                return "メンバーをミュート";
            case VOICE_DEAF_OTHERS:
                return "メンバーのスピーカーをミュート";
            case VOICE_MOVE_OTHERS:
                return "メンバーを移動";
            // イベントの管理がまだJDAに追加されていません
            case ADMINISTRATOR:
                return "管理者";
            case MANAGE_PERMISSIONS:
                return "権限の管理";
            case VIEW_GUILD_INSIGHTS:
                return "サーバーインサイトを見る";
            case REQUEST_TO_SPEAK:
                return "スピーカーの参加をリクエスト";
            default:
                return permission.getName();
        }
    }

    public void stop(Guild guild) {
        AudioTrackData audioTrack = MusicManager.getMusicManager().getMusicData(guild).getTrackScheduler().getAudioTrack();

        if (audioTrack != null && audioTrack.getNicoVideo() != null && audioTrack.getNicoVideo().isSuccess()) {
            audioTrack.getNicoVideo().setBreak(true);
        }

        if (MusicManager.getMusicManager().hasMusicData(guild))
            MusicManager.getMusicManager().getMusicData(guild).getAudioPlayer().destroy();

        MusicManager.musicData.remove(guild.getIdLong());
        VoiceManager.voiceData.remove(guild.getIdLong());
        VoiceRoidStartCommand.textChannelId.remove(guild.getIdLong());

        guild.getAudioManager().closeAudioConnection();
    }

    public VoiceEnum getVoiceEnum(Guild guild) {
        if (MusicManager.getMusicManager().hasMusicData(guild)) {
            return VoiceEnum.MUSIC;
        } else if (VoiceManager.getVoiceManager().hasVoiceData(guild)) {
            return VoiceEnum.VOICE_ROID;
        } else {
            return VoiceEnum.NONE;
        }
    }

    public boolean getVoiceChoice(VoiceEnum voiceEnum, Guild guild) {
        return getVoiceEnum(guild) == voiceEnum;
    }
}