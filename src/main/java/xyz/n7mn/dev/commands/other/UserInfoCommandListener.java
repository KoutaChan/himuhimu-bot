package xyz.n7mn.dev.commands.other;

import me.koply.kcommando.internal.OptionType;
import me.koply.kcommando.internal.annotations.HandleSlash;
import me.koply.kcommando.internal.annotations.Option;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import xyz.n7mn.dev.util.CommonUtils;

public class UserInfoCommandListener {
    @HandleSlash(name = "user", desc = "ユーザー情報を取得します", options = @Option(type = OptionType.USER, name = "target"))
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        Member member = getOrDefault(event.getGuild(), event.getOption("target", OptionMapping::getAsUser), event.getMember());

    }

    public Member getOrDefault(Guild guild, User user, Member member) {
        return user != null ? guild.getMember(user) : member;
    }
}