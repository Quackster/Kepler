package net.h4bbo.kepler.game.player;

public class Wardrobe {
    private int slotId;
    private String sex;
    private String figure;

    public Wardrobe(int slotId, String sex, String figure) {
        this.slotId = slotId;
        this.sex = sex;
        this.figure = figure;
    }

    public int getSlotId() {
        return slotId;
    }

    public String getSex() {
        return sex;
    }

    public String getFigure() {
        return figure;
    }
}
