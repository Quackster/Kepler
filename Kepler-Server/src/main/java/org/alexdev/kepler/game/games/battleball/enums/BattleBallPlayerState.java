package org.alexdev.kepler.game.games.battleball.enums;

public enum BattleBallPlayerState {
    NORMAL(0),
    STUNNED(1),
    TURBO_BOOST(2),
    HIGH_JUMPS(3),
    CLEANING_TILES(4),
    COLORING_FOR_OPPONENT(5),
    CLIMBING_INTO_CANNON(6),
    FLYING_THROUGH_AIR(7),
    BALL_BROKEN(8);

    /*  STATE_NORMAL = 0
  STATE_STUNNED = 1
  STATE_TURBO_BOOST = 2
  STATE_HIGH_JUMPS = 3
  STATE_CLEANING_TILES = 4
  STATE_COLORING_FOR_OPPONENT = 5
  STATE_CLIMBING_INTO_CANNON = 6
  STATE_FLYING_THROUGH_AIR = 7
  STATE_BALL_BROKEN = 8  */

    private final int stateId;

    BattleBallPlayerState(int stateId) {
        this.stateId = stateId;
    }

    public int getStateId() {
        return stateId;
    }
}
