package xyz.n7mn.dev.commands.other;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import xyz.n7mn.dev.managers.slash.Option;
import xyz.n7mn.dev.managers.slash.SlashCommand;
import xyz.n7mn.dev.managers.slash.SlashCommandListener;
import xyz.n7mn.dev.managers.slash.SlashCommandManager;

import java.util.Map;

public class HelpCommand extends SlashCommandListener {
    @SlashCommand(name = "help", description = "ヘルプを表示します", options = @Option(name = "page", description = "ページ", type = OptionType.INTEGER))
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        StringSelectMenu.Builder menu = StringSelectMenu.create("help-selection");
        for (Map.Entry<String, SlashCommandManager.SlashCommandImpl> value : SlashCommandManager.getListeners().entrySet()) {
            if (menu.addOption(value.getKey(), value.getKey()).getOptions().size() <= 25) {
                break;
            }
        }
    }
}