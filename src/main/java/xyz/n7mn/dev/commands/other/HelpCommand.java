package xyz.n7mn.dev.commands.other;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import xyz.n7mn.dev.managers.slash.*;

import java.util.Arrays;
import java.util.List;

public class HelpCommand extends SlashCommandListener {
    private final List<SubCommandType> general = Arrays.asList();
    private final List<SubCommandType> music = Arrays.asList(SubCommandType.MUSIC, SubCommandType.SETTINGS_MUSIC);
    private final List<SubCommandType> casino = Arrays.asList(SubCommandType.CASINO);
    private final List<SubCommandType> earthQuake = Arrays.asList(SubCommandType.EARTHQUAKE, SubCommandType.SETTINGS_EARTHQUAKE);
    private final List<SubCommandType> settings = Arrays.asList(SubCommandType.SETTINGS, SubCommandType.SETTINGS_MUSIC, SubCommandType.SETTINGS_EARTHQUAKE);
    private final List<SubCommandType> other = Arrays.asList(SubCommandType.NONE);

    @SlashCommand(name = "help", description = "ヘルプを表示します", options = {@Option(name = "category", description = "カテゴリー", type = OptionType.STRING, stringChoices = {
            @StringChoice(name = "General", value = "general"),
            @StringChoice(name = "Music", value = "music"),
            @StringChoice(name = "Casino", value = "casino"),
            @StringChoice(name = "EarthQuake", value = "earthQuake"),
            @StringChoice(name = "Other", value = "other"),
            @StringChoice(name = "Settings", value = "settings"),
    }, autoComplete = true)})
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        String category = event.getOption("category", OptionMapping::getAsString);
        List<SubCommandType> subCommandTypes = getOptions(category);
        event.reply(subCommandTypes.toString()).queue();
    }

    public List<SubCommandType> getOptions(String raw) {
        try {
            return (List<SubCommandType>) this.getClass().getDeclaredField(raw).get(this);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}