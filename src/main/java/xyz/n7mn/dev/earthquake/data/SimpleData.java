package xyz.n7mn.dev.earthquake.data;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class SimpleData {
    private String maxInt;
    private String maxString;
    private List<List<String>> city = new ArrayList<>();

    public void addCity(List<String> cityList) {
        city.add(cityList);
    }
}
