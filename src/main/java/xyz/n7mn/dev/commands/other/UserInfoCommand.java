package xyz.n7mn.dev.commands.other;

import xyz.n7mn.dev.managers.slash.SlashCommandListener;

public class UserInfoCommand extends SlashCommandListener {
    /*@SlashCommand(name = "help", description = "ユーザー情報を取得します", options = @Option(type = OptionType.USER, name = "target", description = "対象ユーザーを指定してください"))
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        User mentions = event.getOption("target", OptionMapping::getAsUser);
        Member member = getMember(event.getGuild(), mentions);
        event.replyEmbeds(member != null ? createWithMember(member) : createWithUser(mentions)).queue();
    }

    public MessageEmbed createWithMember(Member member) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(member.getEffectiveName())
                .setImage(member.getEffectiveAvatarUrl());
        if (member.getUser().getFlags().isEmpty()) {
            embed.addField("バッジ", "N/A", false);
        } else {
            embed.addField("バッジ", DiscordUtil.toString(member.getUser().getFlags()), false);
        }
        return embed.build();
    }

    public MessageEmbed createWithUser(User user) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(user.getName())
                .setImage(user.getEffectiveAvatarUrl());
        if (user.getFlags().isEmpty()) {
            embed.addField("バッジ", "N/A", false);
        } else {
            embed.addField("バッジ", DiscordUtil.toString(user.getFlags()), false);
        }
        return embed.build();
    }*/
}