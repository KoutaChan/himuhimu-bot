package xyz.n7mn.dev.util;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import xyz.n7mn.dev.voice.AudioData;
import xyz.n7mn.dev.voice.AudioManager;

import java.awt.*;
import java.util.Collection;

@UtilityClass
public class DiscordUtil {
    public String translatePermission(Permission permission) {
        return switch (permission) {
            case VIEW_CHANNEL -> "チャンネルを見る";
            case MANAGE_CHANNEL -> "チャンネルの管理";
            case MANAGE_ROLES -> "ロールの管理";
            case MANAGE_EMOJIS_AND_STICKERS -> "絵文字・スタンプの管理";
            case VIEW_AUDIT_LOGS -> "監査ログ";
            case MANAGE_WEBHOOKS -> "ウェブフックの管理";
            case MANAGE_SERVER -> "サーバーの管理";
            case CREATE_INSTANT_INVITE -> "招待を作成";
            case NICKNAME_CHANGE -> "ニックネームの変更";
            case NICKNAME_MANAGE -> "ニックネームの管理";
            case KICK_MEMBERS -> "メンバーのキック";
            case BAN_MEMBERS -> "メンバーをBAN";
            case MODERATE_MEMBERS -> "メンバーをタイムアウト";
            case MESSAGE_SEND -> "メッセージを送信";
            case MESSAGE_SEND_IN_THREADS -> "スレッドでメッセージを送信";
            case CREATE_PUBLIC_THREADS -> "公開スレッドを作成";
            case CREATE_PRIVATE_THREADS -> "プライベートスレッドの作成";
            case MESSAGE_EMBED_LINKS -> "埋め込みリンク";
            case MESSAGE_ATTACH_FILES -> "ファイルを添付";
            case MESSAGE_ADD_REACTION -> "リアクションの追加";
            case MESSAGE_EXT_EMOJI -> "外部の絵文字を使用する";
            case MESSAGE_EXT_STICKER -> "外部のスタンプを使用する";
            case MESSAGE_MENTION_EVERYONE -> "@everyone、@here、すべてのロールにメンション";
            case MESSAGE_MANAGE -> "メッセージの管理";
            case MANAGE_THREADS -> "スレッドの管理";
            case MESSAGE_HISTORY -> "メッセージ履歴を読む";
            case MESSAGE_TTS -> "テキスト読み上げメッセージを送信";
            case USE_APPLICATION_COMMANDS -> "アプリコマンドを使う";
            case VOICE_CONNECT -> "ボイスチャンネルに接続";
            case VOICE_SPEAK -> "ボイスチャンネルでの発言";
            case VOICE_STREAM -> "WEB カメラ";
            case VOICE_START_ACTIVITIES -> "アクティビティ";
            case VOICE_USE_VAD -> "音声検出";
            case PRIORITY_SPEAKER -> "優先スピーカー";
            case VOICE_MUTE_OTHERS -> "メンバーをミュート";
            case VOICE_DEAF_OTHERS -> "メンバーのスピーカーをミュート";
            case VOICE_MOVE_OTHERS -> "メンバーを移動";
            // イベントの管理がまだJDAに追加されていません
            case ADMINISTRATOR -> "管理者";
            case MANAGE_PERMISSIONS -> "権限の管理";
            case VIEW_GUILD_INSIGHTS -> "サーバーインサイトを見る";
            case REQUEST_TO_SPEAK -> "スピーカーの参加をリクエスト";
            default -> permission.getName();
        };
    }

    public static String toString(Collection<?> objects) {
        StringBuilder builder = new StringBuilder();
        for (Object object : objects) {
            if (!builder.isEmpty()) {
                builder.append("\n");
            }
            builder.append(object);
        }
        return builder.toString();
    }

    public void stop(Guild guild) {
        AudioData data = AudioManager.getAudio(guild);
        if (data != null) {
            data.getListener().exit();
            data.remove();
        }
    }

    public EmbedBuilder getErrorEmbeds(String reason) {
        return new EmbedBuilder()
                .setTitle("[!] エラーが発生しました")
                .setColor(Color.RED)
                .addField("エラー内容: ", reason, false);
    }
}