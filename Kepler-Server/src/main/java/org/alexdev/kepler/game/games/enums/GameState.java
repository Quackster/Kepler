package org.alexdev.kepler.game.games.enums;

public enum GameState {
    WAITING(0),
    STARTED(1),
    ENDED(2);

    private final int stateId;

    GameState(int stateId) {
        this.stateId = stateId;
    }

    public int getStateId() {
        return stateId;
    }
}
