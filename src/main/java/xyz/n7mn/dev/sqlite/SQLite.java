package xyz.n7mn.dev.sqlite;

import lombok.Getter;

@Getter
public class SQLite {
    public static SQLite INSTANCE = new SQLite();
    //private final EarthQuakeConnection earthQuake = new EarthQuakeConnection();
    private final CasinoDB casino = new CasinoDB();
    private final MusicDB music = new MusicDB();
}