package xyz.n7mn.dev.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import xyz.n7mn.dev.commandprocessor.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@CommandName(command = {"h.help"}, channelType = ChannelType.TEXT, commandType = CommandType.GENERAL)
public class HelpCommand extends Command {

    @Override
    public void CommandEvent(DiscordData e) {
        if (e.getArgs().length == 1) {
            try {
                StringBuilder GENERAL = new StringBuilder();
                StringBuilder FUN = new StringBuilder();
                StringBuilder CASINO = new StringBuilder();
                StringBuilder MUSIC = new StringBuilder();
                StringBuilder EARTHQUAKE = new StringBuilder();
                StringBuilder OTHER = new StringBuilder();
                for (Class<? extends Command> command : CommandManager.command) {
                    CommandName annotation = command.getAnnotation(CommandName.class);


                    try {
                        switch (annotation.commandType()) {
                            case GENERAL:
                                if (GENERAL.length() == 0) GENERAL.append(String.format("`%s`", annotation.command()[0]));
                                else GENERAL.append(String.format(", `%s`", annotation.command()[0]));
                                break;
                            case FUN:
                                if (FUN.length() == 0) FUN.append(String.format("`%s`", annotation.command()[0]));
                                else FUN.append(String.format(", `%s`", annotation.command()[0]));
                                break;
                            case CASINO:
                                if (CASINO.length() == 0) CASINO.append(String.format("`%s`", annotation.command()[0]));
                                else CASINO.append(String.format(", `%s`", annotation.command()[0]));
                                break;
                            case MUSIC:
                                if (MUSIC.length() == 0) MUSIC.append(String.format("`%s`", annotation.command()[0]));
                                else MUSIC.append(String.format(", `%s`", annotation.command()[0]));
                                break;
                            case EARTHQUAKE:
                                if (EARTHQUAKE.length() == 0) EARTHQUAKE.append(String.format("`%s`", annotation.command()[0]));
                                else EARTHQUAKE.append(String.format(", `%s`", annotation.command()[0]));
                                break;
                            case OTHER:
                                if (OTHER.length() == 0) OTHER.append(String.format("`%s`", annotation.command()[0]));
                                else OTHER.append(String.format(", `%s`", annotation.command()[0]));
                                break;
                        }
                    } catch (Exception ignore) {

                    }
                }

                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("?????????????????????BOT?????????????????????")
                        .setDescription("?????????????????????????????????????????????????????? h.help {Command} ????????? h.help all ???????????????????????????")
                        .setColor(Color.YELLOW)
                        .addField(":tools: General", GENERAL.toString(), false)
                        .addField(":roller_coaster: Fun", FUN.toString(), false)
                        .addField(":coin: Casino", CASINO.toString(), false)
                        .addField(":musical_note: Music", MUSIC.toString(), false)
                        .addField(":earth_asia: EarthQuake", EARTHQUAKE.toString(), false)
                        .addField(":question: Other", OTHER.toString(), false);

                e.getTextChannel().sendMessageEmbeds(embedBuilder.build())
                        .setActionRow(
                                Button.link("https://discord.com/api/oauth2/authorize?client_id=" + e.getJda().getSelfUser().getId() + "&permissions=8&scope=bot", "BOT?????????"),
                                Button.link("https://github.com/KoutaChan/himuhimu-bot", "?????????????????? (GitHub)"))
                        .queue();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getArgs().length == 2) {

            String find = e.getArgs()[1];

            if (find.equalsIgnoreCase("all")) {
                final List<String> general = new ArrayList<>();
                final List<String> fun = new ArrayList<>();
                final List<String> casino = new ArrayList<>();
                final List<String> music = new ArrayList<>();
                final List<String> earthQuake = new ArrayList<>();
                final List<String> other = new ArrayList<>();

                music.add(":warning: ???????????????????????????????????????????????????????????????");
                music.add(":exclamation: ?????????: ?????????????????????????????????");

                earthQuake.add(":warning: ??????????????????????????????????????????????????????");

                for (Class<? extends Command> command : CommandManager.command) {

                    CommandName annotation = command.getAnnotation(CommandName.class);

                    try {
                        List<String> strings = new ArrayList<>();

                        strings.add("`" + annotation.command()[0] + "`:");
                        strings.add("?????? ??? " + annotation.help().replaceAll("%command%", annotation.command()[0]));

                        for (int i = 1; i < annotation.command().length; i++) {
                            strings.add("?????????????????? ??? " + annotation.command()[i]);
                        }

                        switch (annotation.commandType()) {
                            case GENERAL:
                                general.addAll(strings);
                                break;
                            case FUN:
                                fun.addAll(strings);
                                break;
                            case CASINO:
                                casino.addAll(strings);
                                break;
                            case MUSIC:
                                music.addAll(strings);
                                break;
                            case EARTHQUAKE:
                                earthQuake.addAll(strings);
                                break;
                            case OTHER:
                                other.addAll(strings);
                                break;
                        }
                    } catch (Exception ignore) {

                    }
                }
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("?????????????????????BOT?????????????????????????????????")
                        .setDescription(e.getMessage().getTimeCreated() + "?????????????????????????????????\nh.help <???????????????> ?????????????????????????????????????????????????????????")
                        .setColor(Color.YELLOW)
                        .addField(":tools: General", String.join("\n", general), false)
                        .addField(":roller_coaster: Fun", String.join("\n", fun), false)
                        .addField(":coin: Casino", String.join("\n", casino), false)
                        .addField(":musical_note: Music", String.join("\n", music), false)
                        .addField(":earth_asia: EarthQuake", String.join("\n", earthQuake), false)
                        .addField(":question: Other", String.join("\n", other), false);

                e.getMessage().replyEmbeds(embedBuilder.build()).setActionRow(
                        Button.primary("help-delete", "?????????????????????")
                ).queue();
            } else {
                switch (e.getArgs()[1].toLowerCase()) {
                    case "general":
                    case "fun":
                    case "casino":
                    case "music":
                    case "earthquake":
                    case "other": {
                        CommandType commandType = CommandType.valueOf(e.getArgs()[1].toUpperCase());
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Class<? extends Command> command : CommandManager.command) {
                            CommandName annotation = command.getAnnotation(CommandName.class);
                            if (annotation.commandType() == commandType) {

                                stringBuilder.append("\n`").append(annotation.command()[0]).append("`:");
                                stringBuilder.append("\n?????? ??? ").append(annotation.help().replaceAll("%command%", annotation.command()[0]));
                                for (int i = 1; i < annotation.command().length; i++) {
                                    stringBuilder.append("\n" + "?????????????????? ??? ").append(annotation.command()[i]);
                                }
                            }
                        }

                        EmbedBuilder embedBuilder = new EmbedBuilder()
                                .setTitle(commandType + "???????????????????????????????????????")
                                .setDescription(e.getMessage().getTimeCreated() + "?????????????????????????????????")
                                .setColor(Color.YELLOW)
                                .addField("?????????????????????", stringBuilder.toString(), false);

                        e.getMessage().replyEmbeds(embedBuilder.build()).queue();

                        break;
                    }

                    default: {
                        for (Class<? extends Command> command : CommandManager.command) {
                            CommandName annotation = command.getAnnotation(CommandName.class);
                            if (annotation.commandType() != CommandType.SPECIAL) {
                                for (String cmd : annotation.command()) {
                                    if (cmd.replaceFirst("h.", "").equalsIgnoreCase(find) || cmd.equalsIgnoreCase(find)) {
                                        EmbedBuilder embedBuilder = new EmbedBuilder()
                                                .setTitle(annotation.command()[0] + "?????????????????????")
                                                .setColor(Color.YELLOW)
                                                .setDescription("?????????: ?????????????????????????????????????????????????????????????????????")
                                                .addField("???????????????", annotation.commandType().toString(), true)
                                                .addField("????????????", e.translatePermission(annotation.permission()), true)
                                                .addField("?????????", annotation.help().replaceAll("%command%", annotation.command()[0]), false);

                                        e.getMessage().replyEmbeds(embedBuilder.build()).queue();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}