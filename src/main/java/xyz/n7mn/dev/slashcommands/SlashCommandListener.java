package xyz.n7mn.dev.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class SlashCommandListener {
    public SlashCommandListener(SlashCommandData data) {

    }

    public abstract void onSlashCommandEvent(SlashCommandInteractionEvent event);
}
