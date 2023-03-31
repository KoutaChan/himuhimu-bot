package xyz.n7mn.dev.earthquake;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import xyz.n7mn.dev.earthquake.data.ResultData;
import xyz.n7mn.dev.sqlite.SQLite;

import java.awt.*;
import java.util.Map;

@Getter
public class EarthQuakeUtilities {

    @Getter
    @Setter
    private static ResultData resultData = null;

    public static void sendYahooMessage(JDA instance) {
        if (instance != null) {
            new Thread(() -> {
                Map<String, String> map = SQLite.INSTANCE.getEarthQuake().getLegacyAll();

                map.forEach((g, t) -> {
                    Guild guild = instance.getGuildById(g);
                    GuildMessageChannelUnion textChannel = guild != null ? guild.getChannelById(GuildMessageChannelUnion.class, t) : null;

                    if (textChannel != null && textChannel.canTalk()) {
                        textChannel.sendMessageEmbeds(EarthQuakeUtilities.getResultData().getEmbedBuilder().build()).queue();
                        textChannel.sendMessageEmbeds(EarthQuakeUtilities.getResultData().getTemp().build()).queue();
                    }
                });

                System.out.println("[Yahoo] およそ" + map.size() + " 件送信！");
            }).start();
        }
    }

    public static void changeColor(EmbedBuilder embedBuilder, String sindo) {
        switch (sindo) {
            case "7" -> embedBuilder.setColor(Color.decode("#71368A"));
            case "6+", "6強" -> embedBuilder.setColor(Color.decode("#992D22"));
            case "6-", "6弱" -> embedBuilder.setColor(Color.decode("#E74C3C"));
            case "5+", "5強" -> embedBuilder.setColor(Color.decode("#A84300"));
            case "5-", "5弱" -> embedBuilder.setColor(Color.decode("#E67E22"));
            case "4" -> embedBuilder.setColor(Color.YELLOW);
            case "3" -> embedBuilder.setColor(Color.BLUE);
            case "2" -> embedBuilder.setColor(Color.decode("#3498DB"));
            default -> embedBuilder.setColor(Color.WHITE);
        }
    }

    public static String transformSindo(int sindo) {
        switch (sindo) {
            case 9:
                return "7";
            case 8:
                return "6強";
            case 7:
                return "6弱";
            case 6:
                return "5強";
            case 5:
                return "5弱";
            default: {
                return String.valueOf(sindo);
            }
        }
    }

    public static int transformSindo(String sindo) {
        if (!sindo.isEmpty()) {
            switch (sindo) {
                case "7":
                    return 9;
                case "6+":
                case "6強":
                    return 8;
                case "6-":
                case "6弱":
                    return 7;
                case "5+":
                case "5強":
                    return 6;
                case "5-":
                case "5弱":
                    return 5;
                default: {
                    return Integer.parseInt(sindo);
                }
            }
        }
        return -9;
    }

    public static int transformNum(String num) {
        if (!num.isEmpty()) {
            if (num.contains("最終報")) return 1212;
            else return Integer.parseInt(num.replaceAll("[^0-9]", ""));
        } else {
            return -9;
        }
    }
}
