package org.alexdev.kepler.game.games.battleball.enums;

public enum BattleBallColourState {
    DISABLED(-2),
    DEFAULT(-1),
    RED(0),
    BLUE(1),
    YELLOW(2),
    GREEN(3);

    private final int tileColourId;

    BattleBallColourState(int tileColourId) {
        this.tileColourId = tileColourId;
    }

    public int getColourId() {
        return tileColourId;
    }

    public static BattleBallColourState getColourById(int id) {
        for (BattleBallColourState colour : values()) {
            if (colour.getColourId() == id) {
                return colour;
            }
        }

        return null;
    }
}
