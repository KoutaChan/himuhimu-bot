package xyz.n7mn.dev.command.casino;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;
import xyz.n7mn.dev.sqlite.SQLite;
import xyz.n7mn.dev.sqlite.data.CasinoData;

import java.awt.*;
import java.util.List;

@CommandName(command = {"h.rank"}, channelType = ChannelType.TEXT, commandType = CommandType.CASINO, help = "コインランキング")
public class CoinRankCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        final List<CasinoData> casinoDataList = SQLite.INSTANCE.getCasino().getAll(e.getGuild().getId());
        casinoDataList.sort((o1, o2) -> Long.compare(o2.getCoin(), o1.getCoin()));

        int count = 1, usercount = Integer.MIN_VALUE;
        CasinoData result = null;

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("コインランキング")
                .setDescription(e.getGuild().getName() + "でのランキング")
                .setColor(Color.YELLOW);

        for (CasinoData casinoData : casinoDataList) {
            if (count > 10 && usercount != Integer.MIN_VALUE) break;

            Member member = e.getGuild().getMemberById(casinoData.getUserid());

            if (member != null) {

                if (member.getIdLong() == e.getMember().getIdLong()) {
                    result = casinoData;
                    usercount = count;
                }

                if (count <= 10) {
                    embedBuilder.addField(count + "位 " + (member.getIdLong() == e.getMember().getIdLong() ? member.getEffectiveName() + " (あなたです！)" : member.getEffectiveName()), casinoData.getCoin() + "コイン", false);
                }

                count++;
            }
        }

        if (usercount != Integer.MIN_VALUE) embedBuilder.addField("あなたの順位 - " + usercount + "位", e.getMember().getEffectiveName() + " (" + result.getCoin() + "コイン)", false);

        e.getMessage().replyEmbeds(embedBuilder.build()).queue();
    }
}