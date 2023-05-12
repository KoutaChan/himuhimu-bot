package xyz.n7mn.dev.managers.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.api.requests.restaction.CommandEditAction;
import org.jetbrains.annotations.Nullable;
import xyz.n7mn.dev.HimuHimuMain;
import xyz.n7mn.dev.events.SlashEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SlashCommandManager implements SlashEvent {
    private final static Map<String, SlashCommandImpl> listeners = new HashMap<>();


    @Override
    public void onSlashCommandInteractionEvent(SlashCommandInteractionEvent event) {
        try {
            SlashCommandImpl slashCommand = get(event.getFullCommandName());
            if (slashCommand != null) {
                slashCommand.callMethod.invoke(slashCommand.listener, event);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void register(SlashCommandListener listener) {
        for (Method method : listener.getClass().getMethods()) {
            SlashCommand slashCommand = method.getAnnotation(SlashCommand.class);
            if (slashCommand == null) {
                continue;
            }
            SubCommandType subCommandType = slashCommand.commandType();
            if (subCommandType != SubCommandType.NONE) {
                if (subCommandType.isSubGroup()) {
                    SubCommandType actual = subCommandType.asSubCommandType();
                    SubcommandData data = createSubCommand(slashCommand);
                    Command command = actual.setAsCommand(copyAction(actual.asCommand(), subCommandType.getSubcommandGroupData().getName(), data).complete());
                    //Samples -> settings earthquake register
                    //Samples -> settings earthquake reset
                    listeners.put(command.getName() + " " + subCommandType.getSubcommandGroupData().getName() + " " + data.getName(), new SlashCommandImpl(listener, data, slashCommand, method, command, subCommandType));
                } else {
                    SubcommandData data = createSubCommand(slashCommand);
                    Command command = subCommandType.setAsCommand(copyAction(subCommandType.asCommand()).addSubcommands(data).complete());
                    listeners.put(command.getName() + " " + data.getName(), new SlashCommandImpl(listener, data, slashCommand, method, command, subCommandType));
                }
            } else {
                CommandCreateAction data = HimuHimuMain.getJDA().upsertCommand(slashCommand.name(), slashCommand.description());
                for (Option option : slashCommand.options()) {
                    if (option.type() != OptionType.UNKNOWN) {
                        data.addOption(option.type(), option.name(), option.description(), option.required(), option.type().canSupportChoices() && option.autoComplete());
                    }
                }
                Command command = data.complete();
                listeners.put(command.getFullCommandName(), new SlashCommandImpl(listener, command, slashCommand, method, null, null));
            }
        }
    }

    private static List<SubcommandData> toSubcommandData(List<Command.Subcommand> command) {
        return command.stream().map(d -> {
            SubcommandData data = new SubcommandData(d.getName(), d.getDescription());
            for (Command.Option option : d.getOptions()) {
                data.addOptions(new OptionData(option.getType(), option.getName(), option.getDescription(), option.isRequired(), option.isAutoComplete()).addChoices(option.getChoices()));
            }
            return data;
        }).collect(Collectors.toList());
    }

    private static List<SubcommandGroupData> toSubcommandGroupData(Command command) {
        return command.getSubcommandGroups().stream().map(d -> {
            SubcommandGroupData data = new SubcommandGroupData(d.getName(), d.getDescription());
            return data.addSubcommands(toSubcommandData(d.getSubcommands()));
        }).collect(Collectors.toList());
    }

    public static List<SubcommandGroupData> toSubcommandGroupData(Command command, String apply, SubcommandData assertCommandData) {
        return command.getSubcommandGroups().stream().map(d -> {
            SubcommandGroupData data = new SubcommandGroupData(d.getName(), d.getDescription());
            if (d.getName().equals(apply)) {
                data.addSubcommands(assertCommandData);
            }
            return data.addSubcommands(toSubcommandData(d.getSubcommands()));
        }).collect(Collectors.toList());
    }

    //TODO: USE #apply
    public static CommandEditAction copyAction(Command command, String apply, SubcommandData assertCommandData) {
        return command.editCommand().setDefaultPermissions(command.getDefaultPermissions()).addSubcommands(toSubcommandData(command.getSubcommands())).addSubcommandGroups(toSubcommandGroupData(command, apply, assertCommandData));
    }

    public static CommandEditAction copyAction(Command command) {
        return command.editCommand().setDefaultPermissions(command.getDefaultPermissions()).addSubcommands(toSubcommandData(command.getSubcommands())).addSubcommandGroups(toSubcommandGroupData(command));
    }

    public static SubcommandData createSubCommand(SlashCommand slashCommand) {
        SubcommandData data = new SubcommandData(slashCommand.name(), slashCommand.description());
        for (Option option : slashCommand.options()) {
            if (option.type() != OptionType.UNKNOWN) {
                OptionData optionData = new OptionData(option.type(), option.name(), option.description(), option.required(), option.autoComplete());
                for (StringChoice choice : option.stringChoices()) {
                    //幸い、チョイスは現在Stringでしか使用していません。
                    //なので現在はStringで検索可能です
                    optionData.addChoice(choice.name(), choice.value());
                }
                data.addOptions(optionData);
            }
        }
        return data;
    }

    public static SlashCommandImpl get(String fullCommandName) {
        return listeners.get(fullCommandName);
    }

    public static Map<String, SlashCommandImpl> getListeners() {
        return listeners;
    }

    public static class SlashCommandImpl {
        private final SlashCommandListener listener;
        private final Command owner;
        private final Object slashCommand;
        private final SlashCommand command;
        private final Method callMethod;
        private final SubCommandType subCommandType;

        public SlashCommandImpl(SlashCommandListener listener, Object slashCommand, SlashCommand command, Method method, Command owner, SubCommandType subCommandType) {
            this.listener = listener;
            this.slashCommand = slashCommand;
            this.command = command;
            this.callMethod = method;
            this.owner = owner;
            this.subCommandType = subCommandType;
        }

        public SlashCommandListener getListener() {
            return listener;
        }

        public Object getSlashCommand() {
            return slashCommand;
        }

        public Command asCommand() {
            return (Command) slashCommand;
        }

        public SubcommandData asSubCommandData() {
            return (SubcommandData) slashCommand;
        }

        public SlashCommand getCommand() {
            return command;
        }

        public Method getCallMethod() {
            return callMethod;
        }

        public void remove() {
            if (isSubCommand()) {
                throw new IllegalStateException("Sorry. Not Supporting SubCommand Deletion!");
                /*Command owned = getOwner();
                List<Command.Subcommand> subCommands = owned.getSubcommands().stream().filter(d -> d.getName().equals(asSubCommandData().getName())).toList();
                getOwner().delete().queue(d -> HimuHimuMain.getJDA().upsertCommand(owned.getName(), owned.getDescription()).addSubcommands(subCommands.stream().map(old -> new SubcommandData(old.getName(), old.getDescription()).addOptions(old.getOptions().stream().map(resolved -> new OptionData(resolved.getType(), resolved.getName(), resolved.getDescription(), resolved.isRequired(), resolved.isAutoComplete())).collect(Collectors.toList()))).toList()));*/
            } else {
                asCommand().delete().queue();
            }
        }

        @Nullable
        public Command getOwner() {
            return owner;
        }

        public boolean isSubCommand() {
            return subCommandType != null;
        }
    }
}