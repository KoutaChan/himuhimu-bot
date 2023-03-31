package xyz.n7mn.dev.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeUtils {
    public String convertSecondToFormat(long milliseconds) {
        milliseconds /= 1000;
        final long hour = milliseconds / 3600;
        final long minutes = (milliseconds % 3600) / 60;
        final long second = milliseconds % 60;

        StringBuilder builder = new StringBuilder();
        if (hour != 0) {
            builder.append(hour).append("時間");
        }
        if (minutes != 0) {
            builder.append(minutes).append("分");
        }

        return builder.append(second).append("秒").toString();
    }
}
