package xyz.n7mn.dev.sqlite.earthquake;

public class EarthQuakeRealData extends EarthQuakeData {
    private final EarthQuakeRealDB earthQuake;

    public EarthQuakeRealData(EarthQuakeRealDB earthQuake, long guild, long channel, int level, boolean alert) {
        super(guild, channel, level, alert);
        this.earthQuake = earthQuake;
    }

    @Override
    public void update() {
        earthQuake.update();
    }

    @Override
    public void remove() {

    }
}