package xyz.n7mn.dev.command.other;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;
import xyz.n7mn.dev.util.DiscordUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;

@CommandName(command = {"h.userinfo", "h.ui"}, channelType = ChannelType.TEXT, commandType = CommandType.OTHER, help = "ユーザー情報 h.userinfo で表示\nh.userinfo <id> で他の人のユーザー情報を確認できます")
public class UserInfoCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        if (e.getArgs().length == 1) {
            e.getMessage().replyEmbeds(getEmbedBuilder(e.getMember()).build()).queue();
        } else {
            try {
                Member member = e.getGuild().getMemberById(e.getArgs()[1]);

                if (member == null) {
                    e.getMessage().reply("エラーが発生しました！").queue();
                } else {
                    e.getMessage().replyEmbeds(getEmbedBuilder(member).build()).queue();
                }
            } catch (Exception ignore) {
                e.getMessage().reply("エラーが発生しました！").queue();
            }
        }
    }

    public static EmbedBuilder getEmbedBuilder(Member member) {
        Duration duration = Duration.between(member.getTimeCreated().toInstant(), Instant.now());

        List<Role> roleList = member.getRoles();
        EnumSet<Permission> permissions = member.getPermissions();

        StringBuilder roleStringBuilder = new StringBuilder();
        StringBuilder permissionBuilder = new StringBuilder();

        roleList.forEach(role -> {
            if (roleStringBuilder.length() == 0) {
                roleStringBuilder.append(role.getAsMention());
            } else {
                roleStringBuilder.append(",").append(role.getAsMention());
            }
        });

        permissions.forEach(permission -> {
            if (permissionBuilder.length() == 0) {
                permissionBuilder.append("`").append(DiscordUtil.translatePermission(permission)).append("`");
            } else {
                permissionBuilder.append(",`").append(DiscordUtil.translatePermission(permission)).append("`");
            }
        });

        return new EmbedBuilder()
                .setTitle(member.getEffectiveName() + "の情報")
                .setThumbnail(member.getAvatarUrl())
                .addField("アカウント作成日", duration.toDays() + "日", false)
                .addField("BOT？", member.getUser().isBot() ? "はい" : "いいえ", false)
                .addField("役職 (" + roleList.size() + ")", roleList.size() == 0 ? "あなた役職はありません" : roleStringBuilder.toString(), false)
                .addField("権限 (" + permissions.size() + ")", permissions.size() == 0 ? "あなたに権限はありません" : permissionBuilder.toString(), false);
    }
}