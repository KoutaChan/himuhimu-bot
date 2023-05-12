package xyz.n7mn.dev.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommonUtils {
    public static <T> T onNull(T t, T orDefault) {
        return t == null ? orDefault : t;
    }

    public static byte[] toByteArray(BufferedImage image) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    public static String getProgressBar(long progress, long max) {
        final String progressText = Stream.generate(() -> "▇").limit(progress).collect(Collectors.joining());
        final String emptyProgressText = Stream.generate(() -> "—").limit(max - progress).collect(Collectors.joining());
        return progressText + emptyProgressText;
    }
}
