package xyz.n7mn.dev.sqlite.earthquake;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class EarthQuakeData {
    private long guild, channel;
    private int level,
    private boolean alert;

    public EarthQuakeData(long guild, long channel, int level, boolean alert) {
        this.guild = guild;
        this.channel = channel;
        this.level = level;
        this.alert = alert;
    }

    public abstract void update();
    public abstract void remove();
}
