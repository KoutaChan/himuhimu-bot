package xyz.n7mn.dev.voice.provider.bilibili;

import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.tools.io.PersistentHttpStream;

import java.io.IOException;
import java.net.URI;

public class HimuBiliBiliAudioSourceManager extends PersistentHttpStream {
    /**
     * @param httpInterface The HTTP interface to use for requests
     * @param contentUrl    The URL of the resource
     * @param contentLength The length of the resource in bytes
     */
    public HimuBiliBiliAudioSourceManager(HttpInterface httpInterface, URI contentUrl, Long contentLength) {
        super(httpInterface, contentUrl, contentLength);
    }

    @Override
    public void skipNBytes(long n) throws IOException {
        super.skipNBytes(n);
    }
}
