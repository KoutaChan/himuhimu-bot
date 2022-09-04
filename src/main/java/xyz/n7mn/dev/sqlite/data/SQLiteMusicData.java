package xyz.n7mn.dev.sqlite.data;

import lombok.Getter;
import xyz.n7mn.dev.sqlite.SQLite;

@Getter
public class SQLiteMusicData {

    private final String guildId;
    private int defaultVolume;
    private int maxVolume;

    public SQLiteMusicData(String guild, int defaultVolume, int maxVolume) {
        this.guildId = guild;
        this.defaultVolume = defaultVolume;
        this.maxVolume = maxVolume;
    }

    public SQLiteMusicData setDefaultVolume(int defaultVolume) {
        SQLite.INSTANCE.getMusic().updateDefaultVolume(guildId, defaultVolume);
        this.defaultVolume = defaultVolume;

        return this;
    }

    public SQLiteMusicData setMaxVolume(int maxVolume) {
        SQLite.INSTANCE.getMusic().updateMaxVolume(guildId, maxVolume);
        this.maxVolume = maxVolume;

        return this;
    }
}