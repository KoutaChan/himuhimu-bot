package xyz.n7mn.dev.commandprocessor;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.n7mn.dev.command.HelpCommand;
import xyz.n7mn.dev.command.HelpDMCommand;
import xyz.n7mn.dev.command.PingCommand;
import xyz.n7mn.dev.command.casino.*;
import xyz.n7mn.dev.command.music.*;
import xyz.n7mn.dev.command.earthquake.EarthQuakeInfoCommand;
import xyz.n7mn.dev.command.earthquake.EarthQuakeSetCommand;
import xyz.n7mn.dev.command.fun.ColorCommand;
import xyz.n7mn.dev.command.fun.DiceCommand;
import xyz.n7mn.dev.command.fun.Random;
import xyz.n7mn.dev.command.other.UserInfoCommand;
import xyz.n7mn.dev.command.other.VoiceRoidStartCommand;
import xyz.n7mn.dev.command.other.VoiceRoidStopCommand;
import xyz.n7mn.dev.command.rpg.RPGCommand;
import xyz.n7mn.dev.command.special.SpecialByPoti;
import xyz.n7mn.dev.HimuHimuMain;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {
    public static final List<Class<? extends Command>> command = new ArrayList<>();

    static {
        if (HimuHimuMain.configManager.getBoolean("enable.general")) {
            // General Command
            command.addAll(Arrays.asList(HelpCommand.class,
                    HelpDMCommand.class,
                    PingCommand.class));
        }
        if (HimuHimuMain.configManager.getBoolean("enable.fun")) {
            // Fun Command
            command.addAll(Arrays.asList(DiceCommand.class,
                    Random.class,
                    ColorCommand.class,
                    RPGCommand.class));
        }
        if (HimuHimuMain.configManager.getBoolean("enable.casino")) {
            // Casino Command
            command.addAll(Arrays.asList(CoinCommand.class,
                    CoinRankCommand.class,
                    GameOmikujiCommand.class,
                    GameBlackJackCommand.class,
                    GameSlotCommand.class,
                    GameFXCommand.class,
                    KyuusaiCommand.class));
        }
        if (HimuHimuMain.configManager.getBoolean("enable.music")) {
            // Music Command
            command.addAll(Arrays.asList(MusicPlayCommand.class,
                    MusicRepeatCommand.class,
                    MusicSkipCommand.class,
                    MusicStopCommand.class,
                    MusicPauseCommand.class,
                    MusicResumeCommand.class,
                    MusicVolumeCommand.class,
                    MusicPlayListCommand.class,
                    MusicNowPlayCommand.class,
                    MusicSettingsCommand.class,
                    MusicVolumeDefaultCommand.class,
                    MusicVolumeLimitCommand.class));
        }
        if (HimuHimuMain.configManager.getBoolean("enable.earthquake")) {
            // EarthQuake Command
            command.addAll(Arrays.asList(EarthQuakeSetCommand.class,
                    EarthQuakeInfoCommand.class));
        }
        if (HimuHimuMain.configManager.getBoolean("enable.other")) {
            // Other Command

            //TODO: add more command.
            command.add(UserInfoCommand.class);

            if (HimuHimuMain.configManager.getBoolean("enable.yomiage")) {
                command.addAll(Arrays.asList(VoiceRoidStartCommand.class,
                        VoiceRoidStopCommand.class));
            }
        }

        //Special Command
        command.add(SpecialByPoti.class);
    }

    public static void execute(MessageReceivedEvent e) {
        try {
            for (Class<? extends Command> command : command) {
                CommandName annotation = command.getAnnotation(CommandName.class);

                String[] commandName = annotation.command();

                if (annotation.channelType() == ChannelType.UNKNOWN || e.getMessage().getChannelType() == annotation.channelType()) {
                    for (String cmd : commandName) {
                        String[] split = e.getMessage().getContentRaw().split("\\s+");

                        if (cmd.isEmpty() || split[0].equalsIgnoreCase(cmd) || (annotation.startWith() && split[0].startsWith(cmd))) {
                            if (annotation.maintenance() && HimuHimuMain.configManager.getLong("discord.owner.id") != e.getMember().getIdLong()) {

                                EmbedBuilder embedBuilder = new EmbedBuilder()
                                        .setTitle("このコマンドは現在メンテナンス中です！")
                                        .setColor(Color.RED)
                                        .setDescription(":exclamation: 訳があってこのコマンドはメンテナンス中です")
                                        .addField("理由", annotation.maintenanceMessage(), false);

                                e.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                            } else {
                                if (e.getMember() != null && (annotation.permission()[0] == Permission.UNKNOWN || Arrays.stream(annotation.permission()).anyMatch(i -> e.getMember().hasPermission(i)))) {
                                    Command instance = command.newInstance();

                                    instance.CommandEvent(new DiscordData(e.getChannel().asTextChannel(), e.getMessage(), split));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}