package xyz.n7mn.dev;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import xyz.n7mn.dev.commands.earthquake.EarthQuakePrintCommand;
import xyz.n7mn.dev.commands.earthquake.settings.EarthQuakeRegisterCommand;
import xyz.n7mn.dev.commands.earthquake.settings.EarthQuakeResetCommand;
import xyz.n7mn.dev.commands.earthquake.settings.EarthQuakeValueCommand;
import xyz.n7mn.dev.commands.general.StatusCommand;
import xyz.n7mn.dev.commands.music.*;
import xyz.n7mn.dev.commands.other.HelpCommand;
import xyz.n7mn.dev.earthquake.EarthQuakeYahoo;
import xyz.n7mn.dev.earthquake.EarthQuakeYahooMonitor;
import xyz.n7mn.dev.events.MessageEventManager;
import xyz.n7mn.dev.events.SlashEventManager;
import xyz.n7mn.dev.events.common.AddCoinManager;
import xyz.n7mn.dev.managers.message.MessageCommandManager;
import xyz.n7mn.dev.managers.slash.SlashCommandManager;
import xyz.n7mn.dev.util.ConfigManager;

import java.nio.file.Paths;

public class HimuHimuMain {
    public static ConfigManager configManager = new ConfigManager("config.properties", Paths.get("", "config.properties").toAbsolutePath());
    public static EarthQuakeYahooMonitor earthQuakeYahooMonitor;
    private static JDA jda;

    public static void main(String[] args) {
        try {
            jda = JDABuilder.createDefault(configManager.getString("discord.token"), GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.SCHEDULED_EVENTS)
                    .addEventListeners(new EventListener())
                    .setStatus(OnlineStatus.IDLE)
                    .setActivity(Activity.playing("Starting up | 2.0.0 - Supporting SlashCommand!"))
                    .build();
            //Listeners - HimuHimu Base
            MessageEventManager.addListener(new MessageCommandManager(), new AddCoinManager());
            SlashEventManager.addListener(new SlashCommandManager());
            //Command
            //全部のコマンドを消す: ディスコードは消してくれないため
            //安全のためスレッドを停止して登録させる、偉いよね？
            //ただしこのせいでコマンド登録が遅れると思ってる？
            jda.retrieveCommands().complete().forEach(d -> d.delete().complete());
            SlashCommandManager.register(new HelpCommand());
            SlashCommandManager.register(new StatusCommand());
            SlashCommandManager.register(new MusicPlayCommand());
            SlashCommandManager.register(new MusicRepeatCommand());
            SlashCommandManager.register(new MusicNowPlayCommand());
            SlashCommandManager.register(new MusicSkipCommand());
            SlashCommandManager.register(new MusicStopCommand());
            SlashCommandManager.register(new MusicVolumeCommand());
            SlashCommandManager.register(new EarthQuakeValueCommand());
            SlashCommandManager.register(new EarthQuakeResetCommand());
            SlashCommandManager.register(new EarthQuakeRegisterCommand());
            SlashCommandManager.register(new EarthQuakePrintCommand());
            if (configManager.getBoolean("enable.earthquake")) {
                EarthQuakeYahoo.start(jda);
            }
            if (configManager.getBoolean("enable.earthquake.nied")) {
                earthQuakeYahooMonitor = new EarthQuakeYahooMonitor(jda);
                earthQuakeYahooMonitor.startNewThread();
            }
            jda.getPresence().setStatus(OnlineStatus.ONLINE);
            jda.getPresence().setActivity(Activity.playing("/help | 2.0.0 - Supporting SlashCommand!"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static EarthQuakeYahooMonitor getEarthQuakeYahooMonitor() {
        return earthQuakeYahooMonitor;
    }

    public static JDA getJDA() {
        return jda;
    }
}