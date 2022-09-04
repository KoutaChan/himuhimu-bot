package xyz.n7mn.dev.earthquake;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xyz.n7mn.dev.earthquake.data.ResultData;
import xyz.n7mn.dev.earthquake.data.SimpleData;

import java.io.IOException;
import java.text.DateFormat;
import java.util.List;
import java.util.*;

//TODO: RECODE

public class EarthQuakeYahoo extends Thread {

    public static String lastImageURL = null;

    public static void start(JDA jda) {
        TimerTask task = new TimerTask() {
            public void run() {
                try {
                    String url = "https://typhoon.yahoo.co.jp/weather/jp/earthquake/?t=1";
                    Document document = Jsoup.connect(url)
                            .get();
                    String imageURL = getImageData(document);

                    if (lastImageURL != null) {
                        if (!lastImageURL.equalsIgnoreCase(imageURL)) {
                            if (imageURL == null) {
                                document = Jsoup.connect(url)
                                        .get();
                                imageURL = getImageData(document);
                            }
                            if (imageURL == null) return;

                            EarthQuakeUtilities.setResultData(getEarthQuake(document, imageURL));
                            EarthQuakeUtilities.sendYahooMessage(jda);
                        }
                    } else {
                        EarthQuakeUtilities.setResultData(getEarthQuake(document, imageURL));
                    }

                    lastImageURL = imageURL;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 1000 * 65);
    }

    public static String getImageData(Document document) {
        //https://weather-pctr.c.yimg.jp/t/weather-img/earthquake/20211212123108/17b62736_1639280100_point.png
        //5番目(4番目)ですが、一応チェックします。
        for (String str : document.select("img").attr("alt", "全地点の震度").eachAttr("src")) {
            if (str.contains("https://weather-pctr.c.yimg.jp/t/weather-img/earthquake/")) {
                return str;
            }
        }
        return null;
    }


    public static ResultData getEarthQuake(Document document, String imageURL) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("地震情報")
                .setFooter(DateFormat.getDateTimeInstance().format(new Date()) + "に作成されました | Yahoo地震情報");

        Elements elements = document.getElementsByClass("yjw_table");

        Element small = elements.get(0);

        List<String> info = small.getElementsByTag("small").eachText();

        for (int i = 0; i < info.size() / 2; i++) {
            int copied = i * 2;
            if (i == 2) {
                EarthQuakeUtilities.changeColor(embedBuilder, info.get(copied + 1));
            }
            embedBuilder.addField(info.get(copied), "\n" + info.get(copied + 1), false);
        }

        EmbedBuilder temp = new EmbedBuilder(embedBuilder)
                .setTitle("簡易地震情報")
                .setDescription("")
                .setImage(imageURL);

        Element top = elements.get(1);

        Elements middle = top.getElementsByAttributeValue("valign", "middle");

        List<SimpleData> simpleData = new ArrayList<>();

        middle.forEach(element -> {
            SimpleData simple = new SimpleData();

            Elements value = element.getElementsByAttributeValue("valign", "top");

            String maxString = element.getElementsByTag("small").get(0).text();
            simple.setMaxString(maxString);
            simple.setMaxInt(maxString.replaceAll("震度", ""));

            value.forEach(intensity -> simple.addCity(intensity.getElementsByTag("small").eachText()));

            simpleData.add(simple);
        });

        simpleData.forEach(data -> {
            StringBuilder stringBuilder = new StringBuilder();
            data.getCity().forEach(city -> {
                for (int i = 0; i < city.size() / 2 && i < 3; i++) {
                    int copied = i * 2;
                    stringBuilder.append("\n").append(city.get(copied));
                    stringBuilder.append("```").append(city.get(copied + 1).replaceAll("　 ", "\n")).append("```");
                }
            });
            //max = 1024 * 4
            if (embedBuilder.length() + stringBuilder.length() <= 6000) {
                if (stringBuilder.length() > 1024) {
                    embedBuilder.addField(data.getMaxString() + " > ", stringBuilder.substring(0, 990) + "``` 収まりませんでした...", true);
                } else {
                    embedBuilder.addField(data.getMaxString() + " > ", String.valueOf(stringBuilder), true);
                }
            }
        });
        return new ResultData(embedBuilder, temp);
    }
}