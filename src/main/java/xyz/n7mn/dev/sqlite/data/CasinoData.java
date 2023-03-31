package xyz.n7mn.dev.sqlite.data;

import lombok.Getter;
import xyz.n7mn.dev.sqlite.SQLite;

@Getter
public class CasinoData {

    private final String guild;
    private final String userid;
    private long coin;

    public CasinoData(String guild, String userid, long coin) {
        this.guild = guild;
        this.userid = userid;
        this.coin = coin;
    }

    public CasinoData update(long coin) {
        //無駄にデータベースをアップデートするだけ
        if (coin != 0) {
            this.coin = coin > 0 ? addCoin(coin) : removeCoin(coin);
            SQLite.INSTANCE.getCasino().update(guild, userid, this.coin);
        }
        return this;
    }

    public CasinoData set(long coin) {
        SQLite.INSTANCE.getCasino().update(guild, userid, coin);
        this.coin = coin;
        return this;
    }

    public boolean hasCoin(long coin) {
        return this.coin >= coin && coin > 0;
    }

    public long addCoin(long coin) {
        try {
            return Math.addExact(this.coin, coin);
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }

    public long removeCoin(long coin) {
        try {
            return Math.addExact(this.coin, coin);
        } catch (ArithmeticException e) {
            return Long.MIN_VALUE;
        }
    }
}