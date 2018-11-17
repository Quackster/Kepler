package org.alexdev.kepler.game.games.battleball.enums;

public enum BattleBallTileState {
    DEFAULT(0),
    TOUCHED(1),
    CLICKED(2),
    PRESSED(3),
    SEALED(4);// = 1, CLICKED = 2, PRESSED = 3, SEALED = 4

    private final int tileStateId;

    BattleBallTileState(int tileStateId) {
        this.tileStateId = tileStateId;
    }

    public int getTileStateId() {
        return tileStateId;
    }

    public static BattleBallTileState getStateById(int id) {
        for (BattleBallTileState state : values()) {
            if (state.getTileStateId() == id) {
                return state;
            }
        }

        return null;
    }
}
