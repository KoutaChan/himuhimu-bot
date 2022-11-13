package xyz.n7mn.dev.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class FileUtil {

    public static void copyFromResources(String source, Path destination) {
        if (!destination.toFile().exists()) {
            InputStream sources = FileUtil.class.getResourceAsStream("/" + source);
            try {
                Files.copy(sources, destination);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<String, String> getFileLine(Path path) {

        Map<String, String> key = new HashMap<>();

        try (Stream<String> lines = Files.lines(path, Charset.defaultCharset())) {
            lines.forEachOrdered(line -> {
                if (!line.startsWith("#") && !line.isEmpty()) {
                    String[] split = line.split("=", 2);
                    key.put(split[0], split[1]);
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return key;
    }
}