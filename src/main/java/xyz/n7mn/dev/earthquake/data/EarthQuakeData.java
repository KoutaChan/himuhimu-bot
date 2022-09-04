package xyz.n7mn.dev.earthquake.data;

import lombok.Getter;
import lombok.Setter;
import xyz.n7mn.dev.earthquake.EarthQuakeUtilities;

@Getter @Setter
public class EarthQuakeData {

    private String area;
    private String num;
    private String sindo;

    public EarthQuakeData(String area, String sindo, String num) {
        this.area = area;
        this.sindo = sindo;
        this.num = num;
    }

    public int getSindoValue() {
        return EarthQuakeUtilities.transformSindo(sindo);
    }

    public int getNumValue() {
        return EarthQuakeUtilities.transformNum(num);
    }
}