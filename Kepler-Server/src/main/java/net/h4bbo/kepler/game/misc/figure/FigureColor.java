package net.h4bbo.kepler.game.misc.figure;

public class FigureColor {
    private String colourId;
    private String index;
    private boolean isClubRequired;
    private boolean isSelectable;

    public FigureColor(String colourId, String index, boolean isClubRequired, boolean isSelectable) {
        this.colourId = colourId;
        this.index = index;
        this.isClubRequired = isClubRequired;
        this.isSelectable = isSelectable;
    }

    public String getColourId() {
        return colourId;
    }

    public String getIndex() {
        return index;
    }

    public boolean isClubRequired() {
        return isClubRequired;
    }

    public boolean isSelectable() {
        return isSelectable;
    }
}
