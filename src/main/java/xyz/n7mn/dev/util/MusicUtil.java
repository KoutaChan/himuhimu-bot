package xyz.n7mn.dev.util;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Guild;
import xyz.n7mn.dev.sqlite.SQLite;
import xyz.n7mn.dev.sqlite.data.SQLiteMusicData;

@UtilityClass
public class MusicUtil {
    public SQLiteMusicData getMusicData(String guild) {
        SQLiteMusicData musicData = SQLite.INSTANCE.getMusic().get(guild);
        return musicData != null ? musicData : createMusicData(guild);
    }

    public SQLiteMusicData getMusicData(Guild guild) {
        return getMusicData(guild.getId());
    }

    public SQLiteMusicData createMusicData(String guild) {
        SQLite.INSTANCE.getMusic().insert(guild, 100, 100);

        return new SQLiteMusicData(guild, 100, 100);
    }
}
