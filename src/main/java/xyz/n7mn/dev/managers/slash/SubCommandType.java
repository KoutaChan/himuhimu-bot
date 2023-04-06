package xyz.n7mn.dev.managers.slash;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import xyz.n7mn.dev.HimuHimuMain;

public enum SubCommandType {
    MUSIC(create("music"), null, false),
    EARTHQUAKE(create("earthquake"), null, false),
    SETTINGS(create("settings", Permission.ADMINISTRATOR), null, false),
    SETTINGS_EARTHQUAKE(SETTINGS, new SubcommandGroupData("earthquake", "EarthQuake Settings SubGroups"), true),
    SETTINGS_MUSIC(SETTINGS, new SubcommandGroupData("music", "Music Settings SubGroups"), true),
    NONE(null, null, false);

    private Object command;
    private SubcommandGroupData subcommandGroupData;
    private final boolean subGroup;

    SubCommandType(Object command, SubcommandGroupData data, boolean subGroup) {
        this.command = command;
        this.subcommandGroupData = data;
        this.subGroup = subGroup;
        if (this.subGroup) {
            SubCommandType type = asSubCommandType();
            type.setAsCommand(SlashCommandManager.copyAction(type.asCommand()).addSubcommandGroups(this.subcommandGroupData).complete());
        }
    }

    public Command asCommand() {
        return (Command) command;
    }

    public Command setAsCommand(Command command) {
        if (this == NONE) {
            return command;
        }
        return (Command) (this.command = command);
    }

    public SubCommandType asSubCommandType() {
        return (SubCommandType) command;
    }

    public SubcommandGroupData getSubcommandGroupData() {
        return subcommandGroupData;
    }

    public void setSubcommandGroupData(SubcommandGroupData subcommandGroupData) {
        this.subcommandGroupData = subcommandGroupData;
    }

    public boolean isSubGroup() {
        return subGroup;
    }

    private static Command create(String name, Permission... permission) {
        return HimuHimuMain.getJDA().upsertCommand(name, "Registered By HimuHimu Bot 2.0").setDefaultPermissions(DefaultMemberPermissions.enabledFor(permission)).complete();
    }

    private static Command create(String name) {
        return HimuHimuMain.getJDA().upsertCommand(name, "Registered By HimuHimu Bot 2.0").complete();
    }
}