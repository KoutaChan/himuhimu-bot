package xyz.n7mn.dev.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeUtils {
    public String convertSecondToFormat(long seconds) {
        final long hour = seconds / 3600;
        final long minutes = (seconds % 3600) / 60;
        final long second = seconds / 60;

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
