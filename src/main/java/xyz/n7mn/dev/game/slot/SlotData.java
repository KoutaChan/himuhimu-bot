package xyz.n7mn.dev.game.slot;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SlotData {
    private String name;
    private int number;

    public SlotData(String name, int number) {
        this.name = name;
        this.number = number;
    }
}