package xyz.n7mn.dev.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HtmlUtils {
    public String getString(String str) {
        return str.replaceAll("&quot;", "\"")
                .replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&apos;", "'")
                .replaceAll("&#39;", "'");
    }
}