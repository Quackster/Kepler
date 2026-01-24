package net.h4bbo.kepler.game.misc.figure;

public class FigureSetType {
    private String set;
    private int paletteId;
    private boolean isMandatory;

    public FigureSetType(String set, int paletteId, boolean isMandatory) {
        this.set = set;
        this.paletteId = paletteId;
        this.isMandatory = isMandatory;
    }

    public String getSet() {
        return set;
    }

    public int getPaletteId() {
        return paletteId;
    }

    public boolean isMandatory() {
        return isMandatory;
    }
}
