package xyz.n7mn.dev.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
}
