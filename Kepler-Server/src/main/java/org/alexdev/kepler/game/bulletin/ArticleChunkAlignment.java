package org.alexdev.kepler.game.bulletin;

public enum ArticleChunkAlignment {
    LEFT(0),
    RIGHT(1),
    CENTER(2);

    private final int value;

    ArticleChunkAlignment(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
