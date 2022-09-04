package xyz.n7mn.dev.music.custom;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@Getter @Setter
public class BiliBiliVideo {

    private boolean isSuccess;

    private String videoId, url, title;
    private JSONArray array;

    private static int SUCCESS_CODE = 0;

    public BiliBiliVideo(String videoId) {
        try {
            this.videoId = videoId;

            Document document = Jsoup.connect("https://api.bilibili.com/x/web-interface/view?bvid=" + videoId)
                    .ignoreContentType(true)
                    .get();

            JSONObject api = new JSONObject(document.text());

            if (api.getInt("code") == SUCCESS_CODE) {
                final long cid = api.getJSONObject("data").getLong("cid");

                title = api.getJSONObject("data").getString("title");

                Document play = Jsoup.connect(String.format("https://api.bilibili.com/x/player/playurl?bvid=%s&cid=%s&qn=16", videoId, cid))
                        .ignoreContentType(true)
                        .get();

                JSONObject data = new JSONObject(play.text());
                isSuccess = data.getInt("code") == SUCCESS_CODE;

                if (isSuccess) {
                    array = data.getJSONObject("data").getJSONArray("durl");

                    url = array.getJSONObject(0).getString("url");
                }
            }
        } catch (Exception ignore) {

        }
    }

    public String getTargetURL() {
        return "https://www.bilibili.com/video/" + videoId + "/";
    }
}
