package xyz.n7mn.dev.music.custom;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Timer;
import java.util.TimerTask;

@Getter @Setter
public class NicoVideo {

    /**
     * @credit - https://github.com/akomekagome/SmileMusic/tree/main/python/niconicodl
     */

    private JSONObject apidata, data, postData;
    private boolean isSuccess;
    private String video, url, id, title, subtitle;
    private boolean isBreak;

    public NicoVideo(String url) {
        httpFirst(url, false);
    }

    public NicoVideo(String url, boolean heartbeat) {
        httpFirst(url, heartbeat);
    }

    public void httpFirst(String url, boolean heartbeat) {
        try {
            this.url = url;

            Document document = Jsoup.connect(url)
                    .get();

            title = document.title().replaceFirst(" - ニコニコ動画$", "");
            subtitle = document.title();
            String element = document.getElementById("js-initial-watch-data").outerHtml().replaceAll("&quot;", "\"").replaceAll("<div id=\"js-initial-watch-data\" data-api-data=\"", "").replaceAll("\" hidden></div>", "");
            JSONObject movie = new JSONObject(element).getJSONObject("media").getJSONObject("delivery").getJSONObject("movie");
            JSONObject session = movie.getJSONObject("session");
            Object videosString = session.getJSONArray("videos").get(0);
            Object audiosString = session.getJSONArray("audios").get(0);
            Object heart_beatLifeTime = session.get("heartbeatLifetime");
            Object recipeId = session.get("recipeId");
            Object priority = session.get("priority");
            JSONObject urls = session.getJSONArray("urls").getJSONObject(0);

            boolean isWellKnownPort = urls.getBoolean("isWellKnownPort");
            boolean isSSL = urls.getBoolean("isSsl");
            String wellKnownPort = isWellKnownPort ? "yes" : "no";
            String SSL = isSSL ? "yes" : "no";
            Object token = session.get("token");
            Object signature = session.get("signature");
            Object contentId = session.get("contentId");
            Object authType = session.getJSONObject("authTypes").get("http");
            Object contentKeyTimeout = session.get("contentKeyTimeout");
            Object serviceUserId = session.get("serviceUserId");
            Object playerId = session.get("playerId");


            String createJSON = "{'session': {'content_type': 'movie', 'content_src_id_sets': [{'content_src_ids': [{'src_id_to_mux': {'video_src_ids' :['" + videosString + "'], 'audio_src_ids': ['" + audiosString + "']}}]}], 'timing_constraint': 'unlimited', 'keep_method': {'heartbeat': {'lifetime': " + heart_beatLifeTime + " }}, 'recipe_id': '" + recipeId + "', 'priority': " + priority + ", 'protocol': {'name': 'http', 'parameters': {'http_parameters': {'parameters': {'http_output_download_parameters': {'use_well_known_port': '" + wellKnownPort + "', 'use_ssl': '" + SSL + "', 'transfer_preset': ''}}}}}, 'content_uri': '', 'session_operation_auth': {'session_operation_auth_by_signature': {'token': '" + token + "', 'signature': '" + signature + "'}}, 'content_id': '" + contentId + "', 'content_auth': {'auth_type': '" + authType + "', 'content_key_timeout': " + contentKeyTimeout + ", 'service_id': 'nicovideo', 'service_user_id': '" + serviceUserId + "'}, 'client_info': {'player_id': '" + playerId + "'}}}";

            this.data = new JSONObject(createJSON);

            Document jsoup = Jsoup.connect("https://api.dmc.nico/api/sessions?_format=json")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .requestBody(String.valueOf(data))
                    .ignoreContentType(true)
                    .post();

            this.apidata = new JSONObject(jsoup.body().text());

            this.isSuccess = apidata.getJSONObject("meta").getInt("status") == 201;
            this.id = apidata.getJSONObject("data").getJSONObject("session").getString("id");
            this.video = apidata.getJSONObject("data").getJSONObject("session").getString("content_uri");

            this.postData = new JSONObject().put("session", apidata.getJSONObject("data").getJSONObject("session"));

            if (isSuccess && heartbeat) new Thread(this::heart_beat).start();
        } catch (Exception ignore) {
            isSuccess = false;
        }
    }

    public void heart_beat() {
        //次に30秒ごとにapi送信 (40秒でもいいらしい)
        TimerTask task = new TimerTask() {
            public void run() {

                if (isBreak) {
                    this.cancel();
                } else {
                    new Thread(() -> {
                        try {
                            Jsoup.connect("https://api.dmc.nico/api/sessions/" + id + "?_format=json&_method=PUT")
                                    .header("Content-Type", "application/json")
                                    .header("Accept", "application/json")
                                    .requestBody(String.valueOf(postData))
                                    .ignoreContentType(true)
                                    .post();
                        } catch (Exception e) {
                            this.cancel();
                        }
                    }).start();
                }
            }
        };
        new Timer().scheduleAtFixedRate(task, 0, 1000 * 40);
    }
}