package xyz.n7mn.dev.voice.provider.nico;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpConfigurable;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterfaceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.SneakyThrows;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity.SUSPICIOUS;

public class HimuNicoAudioSourceManager implements AudioSourceManager, HttpConfigurable {
    private HttpInterfaceManager httpInterfaceManager = HttpClientTools.createDefaultThreadLocalManager();
    /* ニコニコのsoから始まるリンクはアニメなど・・レガシー。 */
    private static final String TRACK_URL_REGEX = "^(?:http://|https://|)(?:www\\.|)nicovideo\\.jp/watch/((so|sm)[0-9]+)(?:\\?.*|)$";
    private static final Pattern trackUrlPattern = Pattern.compile(TRACK_URL_REGEX);

    private static final String MY_LIST_URL_REGEX = "^(?:http://|https://|)(?:www\\.|)nicovideo\\.jp/user/([0-9]+)/mylist/([0-9]+)(?:\\?.*|)$";
    private static final Pattern myListUrlPattern = Pattern.compile(MY_LIST_URL_REGEX);

    @Override
    public String getSourceName() {
        return "himu-niconico";
    }

    public static void main(String[] args) {
        /*JDA instance = JDABuilder.createLight("token", GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS)
                .addEventListeners(new EventListener())
                .enableCache(CacheFlag.VOICE_STATE)
                //.enableCache(CacheFlag.EMOTE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setActivity(Activity.playing("h.help all"))
                .build();

        new Thread(() -> {
            try {
                Thread.sleep(3000L);

                AudioData data = AudioManager.createAudio(AudioType.MUSIC, instance.getTextChannelById("521007043755114523"), instance.getVoiceChannelById("547000405197062154"));
                AudioManager.manager.loadItem("https://www.nicovideo.jp/watch/sm41087797", new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        System.out.println("TRACK*");

                        data.getListener().load(track);
                        data.getListener().queue(track);
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {

                    }

                    @Override
                    public void noMatches() {

                    }

                    @Override
                    public void loadFailed(FriendlyException exception) {
                        exception.printStackTrace();
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            while (true) {

            }
        }).start();

        Matcher trackMatcher = trackUrlPattern.matcher("https://www.nicovideo.jp/watch/sm41087797");

        System.out.println(trackMatcher.matches());
        System.out.println(trackMatcher.group(1));
         */
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {
        Matcher trackMatcher = trackUrlPattern.matcher(reference.identifier);

        if (trackMatcher.matches()) {
            return loadTrack(trackMatcher.group(1));
        }/* else {
            Matcher myListMatcher = myListUrlPattern.matcher(reference.identifier);

            if (myListMatcher.matches()) {
                loadMyList(reference.identifier);
            }
        }*/

        return null;
    }

    public AudioTrack loadMyList(String link) {
        try (HttpInterface httpInterface = getHttpInterface()) {
            try (CloseableHttpResponse response = checkStatusCode(httpInterface.execute(new HttpGet(link)))) {
                Document document = Jsoup.parse(response.getEntity().getContent(), StandardCharsets.UTF_8.name(), "", Parser.xmlParser());

                //List<AudioTrack> tracks = getAllTrack(document.getElementById("ContinuousPlayButton MylistMenu-continuous").attr("href"));

                System.out.println(document.getElementById("js-initial-userpage-data").attr("data-environment"));
            }
        } catch (IOException e) {
            throw new FriendlyException("Error occurred when extracting video info.", SUSPICIOUS, e);
        }
        return null;
    }

    public List<AudioTrack> getAllTrack(String url) {
        return null;
    }

    public AudioTrack loadTrack(String pattern) {
        try (HttpInterface httpInterface = getHttpInterface()){
            try (CloseableHttpResponse response = checkStatusCode(httpInterface.execute(new HttpGet(getVideoUrl(pattern))))) {
                return loadTrack(pattern, Jsoup.parse(response.getEntity().getContent(), StandardCharsets.UTF_8.name(), "", Parser.xmlParser()));
            }
        } catch (IOException e) {
            throw new FriendlyException("Error occurred when extracting video info.", SUSPICIOUS, e);
        }
    }

    public AudioTrack loadTrack(String pattern, Document document) {
        JSONObject object = new JSONObject(document.getElementById("js-initial-watch-data").attr("data-api-data"));

        return new HimuNicoAudioTrack(new AudioTrackInfo(object.getJSONObject("video").getString("title"), object.getJSONObject("owner").getString("nickname"), object.getJSONObject("video").getLong("duration") * 1000, pattern, false, getVideoUrl(pattern)), this);
    }

    public String getVideoUrl(String pattern) {
        return "https://www.nicovideo.jp/watch/" + pattern;
    }

    @SneakyThrows
    public static CloseableHttpResponse checkStatusCode(CloseableHttpResponse response) {
        if (!HttpClientTools.isSuccessWithContent(response.getStatusLine().getStatusCode())) {
            throw new IOException("Unexpected response code from video info: " + response.getStatusLine().getStatusCode());
        }
        return response;
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return true;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) throws IOException {

    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) throws IOException {
        return new HimuNicoAudioTrack(trackInfo, this);
    }

    @Override
    public void shutdown() {

    }

    public HttpInterface getHttpInterface() {
        return httpInterfaceManager.getInterface();
    }

    @Override
    public void configureRequests(Function<RequestConfig, RequestConfig> configurator) {
        httpInterfaceManager.configureRequests(configurator);
    }

    @Override
    public void configureBuilder(Consumer<HttpClientBuilder> configurator) {
        httpInterfaceManager.configureBuilder(configurator);
    }
}