package xyz.n7mn.dev.command.earthquake;

import net.dv8tion.jda.api.entities.ChannelType;
import xyz.n7mn.dev.commandprocessor.Command;
import xyz.n7mn.dev.commandprocessor.CommandName;
import xyz.n7mn.dev.commandprocessor.CommandType;
import xyz.n7mn.dev.commandprocessor.DiscordData;
import xyz.n7mn.dev.earthquake.EarthQuakeUtilities;

@CommandName(command = {"h.earthquakeinfo", "h.eqi"}, channelType = ChannelType.TEXT, commandType = CommandType.EARTHQUAKE, help = "最後に来た地震情報を表示します")
public class EarthQuakeInfoCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        if (EarthQuakeUtilities.getResultData().getData() == null) {
            e.getTextChannel().sendMessageEmbeds(EarthQuakeUtilities.getResultData().getTemp().build()).queue();
        } else {
            e.getTextChannel().sendMessageEmbeds(EarthQuakeUtilities.getResultData().getTemp().build())
                    .addFile(EarthQuakeUtilities.getResultData().getData(), EarthQuakeUtilities.getResultData().isGIF() ? "earthquake.gif" : "earthquake.png")
                    .queue();
        }
    }
}