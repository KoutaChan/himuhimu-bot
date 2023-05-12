package xyz.n7mn.dev.earthquake;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import xyz.n7mn.dev.earthquake.data.AnimatedGifEncoder;
import xyz.n7mn.dev.sqlite.EarthQuakeDB;
import xyz.n7mn.dev.sqlite.SQLite;
import xyz.n7mn.dev.util.CommonUtils;
import xyz.n7mn.dev.util.pair.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EarthQuakeYahooMonitor {
    //color data
    public static Color si_20 = Color.decode("#b600d7"); //7
    public static Color si_19 = Color.decode("#c900ba"); //6+
    public static Color si_18 = Color.decode("#db009c"); //6-
    public static Color si_17 = Color.decode("#e30071"); //5+
    public static Color si_16 = Color.decode("#ed0047"); //5-
    public static Color si_15 = Color.decode("#f11520"); //4
    public static Color si_14 = Color.decode("#f53605"); //4
    public static Color si_13 = Color.decode("#fc4c02"); //3
    public static Color si_12 = Color.decode("#ff6200"); //3
    public static Color si_11 = Color.decode("#ff7e00"); //2
    public static Color si_10 = Color.decode("#f99000"); //2
    public static Color si_9 = Color.decode("#ffaf00"); //1
    public static Color si_8 = Color.decode("#fbc300"); //1
    public static Color si_7 = Color.decode("#f4e200"); //1
    public static Color si_6 = Color.decode("#0fb02b"); //0
    public static Color si_5 = Color.decode("#38a477"); //0
    public static Color si_4 = Color.decode("#5ea7ac"); //0
    public static Color si_3 = Color.decode("#71a2cb"); //0
    public static Color si_2 = Color.decode("#89afc8"); //-1
    public static Color si_1 = Color.decode("#90b3ca"); //-2
    public static Color si_0 = Color.decode("#97b7cc"); //-3
    public static Color BACKGROUND_COLOR = Color.decode("#343434");
    // Big Position Map!
    private JSONObject positions;
    // Base Map
    private BufferedImage base;
    // Hypo Image
    private BufferedImage hypo;
    // Is Synced Map Data
    private boolean synced = true;
    // Synced Map Data
    private JSONArray map;
    // EarthQuake Data
    private final Map<String, EarthQuakeBigData> earthQuakes = new HashMap<>();
    // Queue Request Image!
    private final List<Consumer<BufferedImage>> queue = new ArrayList<>();
    // Task and Worker
    private Thread worker;
    private Timer timer;
    // Time Format
    private final SimpleDateFormat formatDays = new SimpleDateFormat("yyyyMMdd");
    private final SimpleDateFormat formatSeconds = new SimpleDateFormat("yyyyMMddHHmmss");
    // Failed Count
    private int retry = 0;
    // Should Update Message?
    private int canEdit;
    // Last Ping = avg. 1000 ms
    private long lastPing;
    // Instance Of JDA
    // TODO: Remove JDA.
    private final JDA JDA;

    public EarthQuakeYahooMonitor(JDA JDA) {
        this.JDA = JDA;

        try {
            this.positions = new JSONObject(new String(IOUtils.toByteArray(this.getClass().getResourceAsStream("/earthquake_area_data.json")), StandardCharsets.UTF_8));

            this.base = ImageIO.read(this.getClass().getResourceAsStream("/base.png"));
            this.hypo = ImageIO.read(this.getClass().getResourceAsStream("/hypo.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new EarthQuakeYahooMonitor(null).startNewThread();
    }

    public void execute() {
        Date date = lastPing != 0 ? retry > 1
                ? new Date(lastPing) : new Date(lastPing + 1000L)
                //Sindo 4 Image 2022/11/09 17:40
                : new Date(1667983225000L);
                //: new Date(ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Tokyo")).toInstant().toEpochMilli());

        try {
            BufferedImage bufferedImage = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(base, 0, 0, null);

            String days = formatDays.format(date);
            String seconds = formatSeconds.format(date);

            String requests = "https://weather-kyoshin.west.edge.storage-yahoo.jp/RealTimeData/%s/%s.json";
            Document document = Jsoup.connect(String.format(requests, days, seconds))
                    .ignoreContentType(true)
                    .get();
            JSONObject jsonObject = new JSONObject(document.text());

            final boolean isEarthQuake = !jsonObject.isNull("hypoInfo");
            // 無駄に実行する必要はありません
            if (!isEarthQuake && this.queue.isEmpty()) {
                this.reset();
                return;
            }
            JSONObject realTimeData = jsonObject.getJSONObject("realTimeData");
            String intensity = realTimeData.getString("intensity");
            byte[] bytes = intensity.getBytes(StandardCharsets.UTF_8);
            getData(map != null && bytes.length == map.length());
            if (map.length() != bytes.length) {
                throw new RuntimeException("Failed Sync");
            }
            // PS WAVE
            JSONObject psWave = jsonObject.optJSONObject("psWave");
            if (isEarthQuake && psWave != null) {
                writePSWave(bufferedImage, psWave);
            }
            // EarthQuake の観測値をまとめるマップ
            Map<Pair<Double, Double>, Integer> hashMap = new HashMap<>();
            for (int i = 0; i < map.length(); i++) {
                //ヤフーリアルタイムモニタの色の表示方法は byteを -100したときに残った数字です。
                //最大で21段階あります
                final int by = bytes[i] - 100;
                //-1は何も表示しません
                if (by >= 0) {
                    final JSONArray array = map.getJSONArray(i);
                    final double latitude = array.getDouble(0);
                    final double longitude = array.getDouble(1);

                    hashMap.put(Pair.of(latitude, longitude), by);
                }
            }
            // Yahooリアルタイムモニタでは震度が大きい順に上から表示されるので、上書きで正円が消えないように。
            // 小さい順にソートする。
            List<Map.Entry<Pair<Double, Double>, Integer>> entries = hashMap.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue))
                    .collect(Collectors.toList());
            // finally, you can write!
            for (Map.Entry<Pair<Double, Double>, Integer> entry : entries) {
                write(g2d, entry.getKey(), entry.getValue());
            }
            writeHypoLocation(g2d, jsonObject);
            g2d.dispose();
            //TODO: ADD Text Options
            BufferedImage finalImage = additionalText(bufferedImage, jsonObject, entries);
            // メッセージを送ったり GIFに変えたりする機構
            if (isEarthQuake) {
                this.checkAndSendMessage(jsonObject, finalImage);
            } else {
                this.reset();
            }
            //Events...
            for (Consumer<BufferedImage> queue : this.queue) {
                queue.accept(finalImage);
            }
            this.queue.clear();
            retry = 0;
        } catch (Exception ex) {
            retry++;
            if (ex instanceof UnknownHostException) {
                if (retry % 10 == 0d) {
                    System.out.printf("[⚠] 不明なホストです、ホスト元またはあなたのネットワークがダウンしている可能性がります%n>>> このエラーが継続している場合は10秒ごとにエラーが送信されます%n");
                }
                date = new Date(ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Tokyo")).toInstant().toEpochMilli());
            } else if (retry >= 10) {
                //What??
                ex.printStackTrace();
            }
        }
        lastPing = date.getTime();
    }

    //3 seconds!
    public boolean canEdit() {
        final boolean canEdit = this.canEdit == 0;
        this.canEdit = canEdit ? 3 : this.canEdit - 1;
        return canEdit;
    }

    public void checkAndSendMessage(JSONObject object, BufferedImage bufferedImage) {
        JSONArray array = object.getJSONObject("hypoInfo").getJSONArray("items");
        //BufferedImage を Byteに変換する。
        byte[] byteImage = CommonUtils.toByteArray(bufferedImage);
        List<String> reportIds = new ArrayList<>() {{
            for (int i = 0; i < array.length(); i++) {
                String reportId = array.getJSONObject(i).getString("reportId");
                EarthQuakeBigData earthQuakeBigData = earthQuakes.computeIfAbsent(reportId, d -> {
                    final EarthQuakeBigData earthQuake = new EarthQuakeBigData();
                    sent(byteImage, earthQuake::addSended);
                    return earthQuake;
                });
                if (canEdit()) {
                    earthQuakeBigData.editMessages(bufferedImage, byteImage);
                } else {
                    earthQuakeBigData.addRaw(bufferedImage);
                }
                add(reportId);
            }
        }};
        // reportIdが含まれていなかったら消す！
        removeAndTry(reportIds);
    }

    public void removeAndTry(List<String> reportIds) {
        earthQuakes.entrySet().stream()
                .filter(key -> reportIds.stream().noneMatch(id -> key.getKey().equals(id)))
                .forEach(entry -> {
                    entry.getValue().asyncEncode();
                    this.earthQuakes.remove(entry.getKey());
                });
    }

    public void reset() {
        removeAndTry(Collections.emptyList());
    }

    public void sent(byte[] bufferedImage, Consumer<Message> onCompletes) {
        List<EarthQuakeDB.EarthQuakeData> data = SQLite.INSTANCE.getEarthQuake().getAll();
        if (data.isEmpty()) {
            return;
        }
        MessageEmbed messageEmbed = new EmbedBuilder()
                .setTitle("地震速報")
                .setDescription("すべての情報は予測です | 大きな誤差・誤検知を含んでいる可能性があります")
                .setFooter(DateFormat.getDateTimeInstance().format(new Date()) + "に作成されました | 防災科研（NIED）| Yahoo")
                .setImage("attachment://earthquake.png")
                .build();

        data.stream().filter(EarthQuakeDB.EarthQuakeData::isAnnounceRealTime)
                .forEachOrdered(earthQuakeData -> {
                    Guild guild = JDA.getGuildById(earthQuakeData.getGuild());
                    GuildMessageChannelUnion textChannel = guild != null ? guild.getChannelById(GuildMessageChannelUnion.class, earthQuakeData.getChannel()) : null;
                    if (textChannel != null && textChannel.canTalk()) {
                        textChannel.sendMessageEmbeds(messageEmbed)
                                .setFiles(FileUpload.fromData(bufferedImage, "earthquake.png"))
                                .queue(onCompletes);
                    }
                });
    }

    public BufferedImage additionalText(BufferedImage bufferedImage, JSONObject object, List<Map.Entry<Pair<Double, Double>, Integer>> entries) {
        BufferedImage info = new BufferedImage(bufferedImage.getWidth() + 500, bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2dInformation = info.createGraphics();

        g2dInformation.setColor(BACKGROUND_COLOR);
        g2dInformation.fillRect(500, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        g2dInformation.drawImage(bufferedImage, 0, 0, null);

        g2dInformation.setColor(Color.WHITE);

        final boolean isEarthQuake = !object.isNull("hypoInfo");

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2dInformation.setRenderingHints(rh);

        g2dInformation.drawString("これはベータ版です！色々な表示が変更される可能性が高いです", bufferedImage.getWidth(), bufferedImage.getHeight() - 20);

        final long epochMilli = ZonedDateTime.parse(object.getJSONObject("realTimeData").getString("dataTime")).toInstant().toEpochMilli();
        final long error = (System.currentTimeMillis() - epochMilli) / 1000;
        String time = DateFormat.getDateTimeInstance().format(epochMilli);

        final int startingPos = bufferedImage.getWidth() + 10;

        g2dInformation.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g2dInformation.drawString(String.format("地震情報 %s (誤差: %d.3秒)", time, error), startingPos, 25);

        //simple line
        drawLine(g2dInformation, Color.GRAY, info.getWidth(), 40, bufferedImage.getWidth(), 40);

        if (isEarthQuake) {
            g2dInformation.setColor(Color.YELLOW);
            g2dInformation.drawString("現在地震速報が発表されています (予報)", startingPos, 65);
            JSONArray items = object.getJSONObject("hypoInfo").getJSONArray("items");
            g2dInformation.setColor(Color.WHITE);
            //2=325
            for (int i = 0; i < items.length() && i < 2; i++) {
                JSONObject item = items.getJSONObject(i);

                int x = startingPos + 10;
                int y = 100 * (i + 1) + (i * 25);

                drawLine(g2dInformation, Color.GRAY, startingPos, y - 25, startingPos, y + 100);
                g2dInformation.drawString(String.format("震源地 >>> %s (%s報)", item.getString("regionName"), getReportNum(item)), x, y);
                g2dInformation.drawString(String.format("予測震度 >>> %s (M %s)", item.getString("calcintensity"), item.getString("magnitude")), x, 25 + y);
                g2dInformation.drawString("予想深さ >>> " + item.getString("depth"), x, 50 + y);
                g2dInformation.drawString((item.getBoolean("isCancel") ? "この地震速報はキャンセルされました" : "この地震速報はキャンセルされていません"), x, 85 + y);
            }
        } else {
            g2dInformation.drawString("現在地震速報は発表されていません", startingPos, 65);
        }
        g2dInformation.setColor(Color.YELLOW);
        for (int i = entries.size(); i > entries.size() - 10 && i > 0; i--) {
            Map.Entry<Pair<Double, Double>, Integer> entry = entries.get(i - 1);

            //観測値の位置情報 無い場合はnullを出すからできるかぎり位置情報を更新すること
            //0=県
            //1=市町村か〇〇県沖
            // 1.(沖になるのは緯度経度で調べたら完全に海にあるため)
            // 2.(ほとんど自動生成なので間違ってるところがあると思うがほとんど正確)
            JSONArray array = positions.optJSONArray(String.valueOf(entry.getKey().getKey()) + entry.getKey().getValue());

            final int size = (entries.size() - i) + 1;
            final int v = isEarthQuake ? (int) (Math.min(2, object.getJSONObject("hypoInfo").getJSONArray("items").length()) * 162.5) : (int) 162.5;

            int y = v + 25 + size * 50;

            if (array != null) {
                //県
                g2dInformation.drawString(String.format("%s ➖ 震度 %s", array.getString(0), getSindo(entry.getValue())), startingPos, y);
                g2dInformation.drawString(array.getString(1), startingPos + 25, y + 25);
            } else {
                g2dInformation.drawString(String.format("不明 ➖ 震度 %s", getSindo(entry.getValue())), startingPos, y);
                g2dInformation.drawString("不明", startingPos + 25, y + 25);
            }
        }
        g2dInformation.dispose();

        return info;
    }

    public String getReportNum(JSONObject object) {
        return object.getBoolean("isFinal") ? "最終" : "第" + object.getString("reportNum");
    }

    public String getSindo(int si) {
        switch (si) {
            case 20 -> {
                return "7";
            }
            case 19 -> {
                return "6+";
            }
            case 18 -> {
                return "6-";
            }
            case 17 -> {
                return "5+";
            }
            case 16 -> {
                return "5-";
            }
            case 15, 14 -> {
                return "4";
            }
            case 13, 12 -> {
                return "3";
            }
            case 11, 10 -> {
                return "2";
            }
            case 9, 8, 7 -> {
                return "1";
            }
            case 6, 5, 4, 3 -> {
                return "0";
            }
            case 2 -> {
                return "-1";
            }
            case 1 -> {
                return "-2";
            }
            case 0 -> {
                return "-3";
            }
            default -> {
                return "??? / 不明な震度";
            }
        }
    }

    public void drawLine(Graphics2D graphics2D, Color color, int x1, int y1, int x2, int y2) {
        Color oldColor = graphics2D.getColor();
        graphics2D.setColor(color);
        graphics2D.drawLine(x1, y1, x2, y2);
        graphics2D.setColor(oldColor);
    }

    //left lng=116.45558291793395
    //right lng=153.74441708206723
    //bottom lat=23.64048885571455
    public void writePSWave(@NotNull BufferedImage bufferedImage, @NotNull JSONObject psWave) {
        JSONArray items = psWave.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.optJSONObject(i);
            if (item == null) {
                return;
            }
            String pRadiusString = item.optString("pRadius");
            String sRadiusString = item.optString("sRadius");
            //Yahoo EarthQuake: なぜか知らないけど、Yahoo EarthQuakeが時々欠損した状態で送ってくる
            if (pRadiusString == null || sRadiusString == null) {
                return;
            }
            Pair<Double, Double> pixelXY = convertGeoToPixel(Double.parseDouble(item.getString("latitude").replaceAll("N", "")), Double.parseDouble(item.getString("longitude").replaceAll("E", ""))
                    , 1200, 900, 116.45558291796095, 153.74441708208371, 23.640487987015774);

            final int pRadius = (int) Double.parseDouble(pRadiusString);
            final int sRadius = (int) Double.parseDouble(sRadiusString);

            Graphics2D graphics2D = bufferedImage.createGraphics();

            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(new Color(255, 0, 0, 50));
            graphics2D.fillOval(pixelXY.getKey().intValue() - (sRadius / 2), pixelXY.getValue().intValue() - (sRadius / 2), sRadius, sRadius);
            graphics2D.setColor(Color.RED);
            graphics2D.drawOval(pixelXY.getKey().intValue() - (sRadius / 2), pixelXY.getValue().intValue() - (sRadius / 2), sRadius, sRadius);

            graphics2D.setColor(new Color(0, 0, 255, 50));
            graphics2D.fillOval(pixelXY.getKey().intValue() - (pRadius / 2), pixelXY.getValue().intValue() - (pRadius / 2), pRadius, pRadius);
            graphics2D.setColor(Color.BLUE);
            graphics2D.drawOval(pixelXY.getKey().intValue() - (pRadius / 2), pixelXY.getValue().intValue() - (pRadius / 2), pRadius, pRadius);

            /*graphics2D.drawImage(hypo, pixelXY.getKey().intValue() - (hypo.getWidth() / 2), pixelXY.getValue().intValue() - (hypo.getHeight() / 2), null);*/
            graphics2D.dispose();
        }
    }

    public void writeHypoLocation(Graphics2D graphics2D, JSONObject object) {
        JSONObject psWave = object.optJSONObject("psWave");

        if (psWave != null) {
            JSONArray items = psWave.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);

                Pair<Double, Double> pixelXY = convertGeoToPixel(Double.parseDouble(item.getString("latitude").replaceAll("N", "")), Double.parseDouble(item.getString("longitude").replaceAll("E", ""))
                        , 1200, 900, 116.45558291796095, 153.74441708208371, 23.640487987015774);

                //Hypo Location
                graphics2D.drawImage(hypo, pixelXY.getKey().intValue() - (hypo.getWidth() / 2), pixelXY.getValue().intValue() - (hypo.getHeight() / 2), null);
            }
        }
    }

    public void startNewThread() {
        worker = new Thread(this::start);
        worker.start();
    }

    public List<Consumer<BufferedImage>> getQueue() {
        return queue;
    }

    public long getLastPing() {
        return this.lastPing;
    }

    public void setDate(Date date) {
        this.lastPing = date.getTime();
    }

    public void setDate(long time) {
        this.lastPing = time;
    }

    public void start() {
        stop();
        TimerTask task = new TimerTask() {
            public void run() {
                execute();
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void stop() {
        if (worker != null && !worker.isInterrupted() && worker.isAlive()) {
            worker.interrupt();
        }
        if (timer != null) {
            timer.cancel();
        }
    }


    public void getData(boolean f) throws IOException {
        if (!synced && !f) {
            throw new RuntimeException("Failed Sync");
        } else if (!f) {
            Document document = Jsoup.connect("https://weather-kyoshin.west.edge.storage-yahoo.jp/SiteList/sitelist.json")
                    .ignoreContentType(true)
                    .get();
            map = new JSONObject(document.text()).getJSONArray("items");
        }
        synced = f;
    }

    public void write(Graphics2D graphics2D, Pair<Double, Double> pair, int si) {
        try {
            Pair<Double, Double> pixelXY = convertGeoToPixel(pair.getKey(), pair.getValue()
                    , 1200, 900, 116.45558291796095, 153.74441708208371, 23.640487987015774);

            Color color = (Color) getClass().getField("si_" + si).get(null);

            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setPaint(color);

            final int max = si > 6 ? 6 : 4;
            graphics2D.fillOval(pixelXY.getKey().intValue(), pixelXY.getValue().intValue(), max, max);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * THANK YOU
     *
     * @credit: https://stackoverflow.com/questions/2103924/mercator-longitude-and-latitude-calculations-to-x-and-y-on-a-cropped-map-of-the
     */
    public static Pair<Double, Double> convertGeoToPixel(double latitude, double longitude, double mapWidth, double mapHeight, double mapLngLeft, double mapLngRight, double mapLatBottom) {
        double mapLatBottomRad = Math.toRadians(mapLatBottom);
        double latitudeRad = Math.toRadians(latitude);
        double mapLngDelta = (mapLngRight - mapLngLeft);

        double worldMapWidth = ((mapWidth / mapLngDelta) * 360) / (2 * Math.PI);
        double mapOffsetY = (worldMapWidth / 2 * Math.log((1 + Math.sin(mapLatBottomRad)) / (1 - Math.sin(mapLatBottomRad))));

        double x = (longitude - mapLngLeft) * (mapWidth / mapLngDelta);
        double y = mapHeight - ((worldMapWidth / 2 * Math.log((1 + Math.sin(latitudeRad)) / (1 - Math.sin(latitudeRad)))) - mapOffsetY);

        return new Pair<>(x, y);
    }

    private static class EarthQuakeBigData {
        private List<Message> sended = new ArrayList<>();
        private List<BufferedImage> raw = new ArrayList<>();
        private long length = 0;

        public List<BufferedImage> getRaw() {
            return raw;
        }

        public List<Message> getSended() {
            return sended;
        }

        public void addSended(Message message) {
            this.sended.add(message);
        }

        public void addRaw(BufferedImage bufferedImage) {
            this.raw.add(bufferedImage);
        }

        public void editMessages(BufferedImage bufferedImage, byte[] byteImage) {
            this.sended.forEach(action -> action.editMessageEmbeds(action.getEmbeds())
                    .setFiles(FileUpload.fromData(byteImage, "earthquake.png"))
                    .queue());
            length += byteImage.length;
            if (length <= 25000000) {
                raw.add(bufferedImage);
            }
        }

        public void asyncEncode() {
            new Thread(this::encode).start();
        }

        public void encode() {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            AnimatedGifEncoder encoder = new AnimatedGifEncoder();
            encoder.setFrameRate(1);
            encoder.start(output);
            for (BufferedImage bufferedImage : this.raw) {
                encoder.addFrame(bufferedImage);
            }
            if (encoder.finish()) {
                byte[] bytes = output.toByteArray();
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("地震速報 [GIF REPLAY!]")
                        .setDescription("すべての情報は予測です | 大きな誤差・誤検知を含んでいる可能性があります")
                        .setFooter(DateFormat.getDateTimeInstance().format(new Date()) + "に作成されました | 防災科研（NIED）| Yahoo")
                        .setImage("attachment://earthquake.gif");
                if (length > 25000000) {
                    embedBuilder.addField("⚠途中で途切れている可能性があります", "サイズが足りませんでした (25MB)", false);
                }
                this.sended.forEach(action -> action.editMessageEmbeds(embedBuilder.build())
                        .setFiles(FileUpload.fromData(bytes, "earthquake.gif"))
                        .queue());
            }
            remove();
        }

        public void remove() {
            this.sended = null;
            this.raw = null;
            this.length = 0;
        }
    }
}