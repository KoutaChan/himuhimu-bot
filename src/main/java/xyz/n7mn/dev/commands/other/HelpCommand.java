package xyz.n7mn.dev.commands.other;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import xyz.n7mn.dev.managers.slash.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HelpCommand extends SlashCommandListener {
    private final List<SubCommandType> general = Arrays.asList();
    private final List<SubCommandType> music = Arrays.asList(SubCommandType.MUSIC, SubCommandType.SETTINGS_MUSIC);
    private final List<SubCommandType> casino = Arrays.asList(SubCommandType.CASINO);
    private final List<SubCommandType> earthQuake = Arrays.asList(SubCommandType.EARTHQUAKE, SubCommandType.SETTINGS_EARTHQUAKE);
    private final List<SubCommandType> settings = Arrays.asList(SubCommandType.SETTINGS, SubCommandType.SETTINGS_MUSIC, SubCommandType.SETTINGS_EARTHQUAKE);
    private final List<SubCommandType> other = Arrays.asList(SubCommandType.NONE);

    @SlashCommand(name = "help", description = "ヘルプを表示します", options = {
            @Option(name = "category", description = "カテゴリー", type = OptionType.STRING, stringChoices = {
                    @StringChoice(name = "General", value = "general"),
                    @StringChoice(name = "Music", value = "music"),
                    @StringChoice(name = "Casino", value = "casino"),
                    @StringChoice(name = "EarthQuake", value = "earthQuake"),
                    @StringChoice(name = "Other", value = "other"),
                    @StringChoice(name = "Settings", value = "settings"),
            }, required = false),
            @Option(name = "ephemeral", description = "表示", type = OptionType.STRING, stringChoices = {
                    @StringChoice(name = "する", value = "on"),
                    @StringChoice(name = "しない", value = "off")
            }, required = false)
    })
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        String category = event.getOption("category", OptionMapping::getAsString);
        String ephemeral = event.getOption("ephemeral", OptionMapping::getAsString);
        event.replyEmbeds(getHelpEmbed(category != null ? getOptions(category) : null))
                .setEphemeral(ephemeral == null || ephemeral.equalsIgnoreCase("しない"))
                .queue();
    }

    public MessageEmbed getHelpEmbed(List<SubCommandType> subCommandTypes) {
        EmbedBuilder messageEmbed = new EmbedBuilder().setDescription("[O] = オプション/Optional | [R] = 必要/Required");
        for (Map.Entry<String, SlashCommandManager.SlashCommandImpl> slashCommand : SlashCommandManager.getListeners().entrySet()) {
            SlashCommandManager.SlashCommandImpl value = slashCommand.getValue();
            if (subCommandTypes == null || subCommandTypes.contains(value.getSubCommandType())) {
                StringBuilder produced = new StringBuilder("/" + slashCommand.getKey());
                Object rawObject = value.getSlashCommand();
                // SlashCommandは SubCommand と Command どっちかが含まれているのでこういう処理になる。
                if (rawObject instanceof SubcommandData subcommandData) {
                    for (OptionData option : subcommandData.getOptions()) {
                        produced.append(" [").append(option.getDescription()).append("]");
                    }
                } else if (rawObject instanceof Command command) {
                    for (Command.Option option : command.getOptions()) {
                        produced.append(" [").append((option.isRequired() ? "(R) " : "(O) ")).append(option.getDescription()).append("]");
                    }
                }
                messageEmbed.addField(produced.toString(), value.getCommand().description(), false);
            }
        }
        if (messageEmbed.isEmpty()) {
            messageEmbed.setDescription("ごめんなさい、コマンドを見つけられませんでした・・・");
        }
        return messageEmbed.build();
    }

    public List<SubCommandType> getOptions(String raw) {
        if (raw.equalsIgnoreCase("general")) {
            return general;
        } else if (raw.equalsIgnoreCase("music")) {
            return music;
        } else if (raw.equalsIgnoreCase("casino")) {
            return casino;
        } else if (raw.equalsIgnoreCase("earthQuake")) {
            return earthQuake;
        } else if (raw.equalsIgnoreCase("other")) {
            return other;
        } else if (raw.equalsIgnoreCase("settings")) {
            return settings;
        } else {
            return null;
        }
    }
}