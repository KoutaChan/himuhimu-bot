package xyz.n7mn.dev.util;

import lombok.experimental.UtilityClass;
import xyz.n7mn.dev.sqlite.SQLite;
import xyz.n7mn.dev.sqlite.data.CasinoData;

@UtilityClass
public class CasinoManager {
    public CasinoData getCasinoData(String guildId, String userId) {
        CasinoData data = SQLite.INSTANCE.getCasino().get(guildId, userId);
        if (data == null) {
            createCasinoData(guildId, userId);
            return new CasinoData(guildId, userId, 0);
        } else {
            return data;
        }
    }

    public void createCasinoData(String guildId, String userId) {
        SQLite.INSTANCE.getCasino().insert(guildId, userId, 0);
    }
}