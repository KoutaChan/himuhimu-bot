package xyz.n7mn.dev.util;

public class CommonUtils {
    public static <T> T onNull(T t, T orDefault) {
        return t == null ? orDefault : t;
    }
}
