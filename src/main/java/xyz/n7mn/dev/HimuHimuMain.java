package xyz.n7mn.dev;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import xyz.n7mn.dev.commands.CommandManager;
import xyz.n7mn.dev.earthquake.EarthQuakeYahoo;
import xyz.n7mn.dev.earthquake.EarthQuakeYahooMonitor;
import xyz.n7mn.dev.message.MessageListener;
import xyz.n7mn.dev.message.MessageListeners;
import xyz.n7mn.dev.message.common.AddCoinManager;
import xyz.n7mn.dev.util.ConfigManager;

import java.nio.file.Paths;

public class HimuHimuMain {
    public static ConfigManager configManager = new ConfigManager("config.properties", Paths.get("", "config.properties").toAbsolutePath());

    public static EarthQuakeYahooMonitor earthQuakeYahooMonitor;

    public static void main(String[] args) {
        try {
            JDA instance = JDABuilder.createLight(configManager.getString("discord.token"), GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS)
                    .addEventListeners(new EventListener())
                    .enableCache(CacheFlag.VOICE_STATE)
                    //.enableCache(CacheFlag.EMOTE)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setActivity(Activity.playing("h.help all"))
                    .build();

            //Listeners
            MessageListeners.addListener(new CommandManager(), new AddCoinManager());

            //Start
            if (configManager.getBoolean("enable.earthquake")) {
                EarthQuakeYahoo.start(instance);
            }

            if (configManager.getBoolean("enable.earthquake.nied")) {
                earthQuakeYahooMonitor = new EarthQuakeYahooMonitor(instance);
                earthQuakeYahooMonitor.startNewThread();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}