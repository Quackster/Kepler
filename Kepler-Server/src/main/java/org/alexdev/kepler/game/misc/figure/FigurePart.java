package org.alexdev.kepler.game.misc.figure;

public class FigurePart {
    private final String id;
    private final String type;
    private final boolean colorable;
    private final int index;

    public FigurePart(String id, String type, boolean colorable, int index) {
        this.id = id;
        this.type = type;
        this.colorable = colorable;
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public boolean isColorable() {
        return colorable;
    }

    public int getIndex() {
        return index;
    }
}
