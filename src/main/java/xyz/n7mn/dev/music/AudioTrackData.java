package xyz.n7mn.dev.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import lombok.Setter;
import xyz.n7mn.dev.music.custom.BiliBiliVideo;
import xyz.n7mn.dev.music.custom.NicoVideo;

import javax.annotation.Nullable;

@Getter @Setter
public class AudioTrackData {

    private AudioTrack audioTrack;

    private NicoVideo nicoVideo;
    private BiliBiliVideo biliBiliVideo;

    private final MusicType musicType;

    private String title;

    private String url;

    private String targetURL;

    public AudioTrackData(@Nullable AudioTrack audioTrack, String url) {
        this.url = url;
        this.targetURL = url;

        this.audioTrack = audioTrack;
        this.musicType = MusicType.AUTO;

        setTitle();
    }

    public AudioTrackData(AudioTrack audioTrack, MusicType type, String url) {
        this.url = url;

        this.audioTrack = audioTrack;
        this.musicType = type;
    }

    public AudioTrackData(AudioTrack audioTrack, NicoVideo nicoVideo, String url) {
        this.url = url;

        this.audioTrack = audioTrack;
        this.nicoVideo = nicoVideo;

        if (nicoVideo != null && nicoVideo.isSuccess()) {
            this.musicType = MusicType.NICONICO;
        } else {
            this.musicType = MusicType.AUTO;
        }

        setTitle();
        setURL();
    }

    public AudioTrackData(AudioTrack audioTrack, BiliBiliVideo biliBiliVideo, String url) {
        this.url = url;

        this.audioTrack = audioTrack;
        this.biliBiliVideo = biliBiliVideo;

        if (biliBiliVideo != null && biliBiliVideo.isSuccess()) {
            this.musicType = MusicType.BILIBILI;
        } else {
            this.musicType = MusicType.AUTO;
        }

        setTitle();
        setURL();
    }

    public void setURL() {
        switch (musicType) {
            case BILIBILI: {
                url = biliBiliVideo.getUrl();
                targetURL = biliBiliVideo.getTargetURL();

                break;
            }
            case NICONICO: {
                url = nicoVideo.getVideo();
                targetURL = nicoVideo.getUrl();

                break;
            }
        }
    }

    private void setTitle() {
        switch (musicType) {
            case NICONICO: {
                title = nicoVideo.getTitle();

                break;
            }
            case BILIBILI: {
                title = biliBiliVideo.getTitle();

                break;
            }
            case AUTO: {
                if (audioTrack != null && audioTrack.getInfo().title != null) {
                    title = audioTrack.getInfo().title;
                }

                break;
            }
        }
    }

    public void setAudioTrack(AudioTrack audioTrack) {
        this.audioTrack = audioTrack;

        if (musicType == MusicType.AUTO) setTitle();
    }
}