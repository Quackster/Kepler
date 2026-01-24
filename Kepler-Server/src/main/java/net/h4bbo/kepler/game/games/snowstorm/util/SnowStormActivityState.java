package net.h4bbo.kepler.game.games.snowstorm.util;

public enum SnowStormActivityState {
    ACTIVITY_STATE_INVINCIBLE_AFTER_STUN(3, 60),
    ACTIVITY_STATE_STUNNED(2, 125),
    ACTIVITY_STATE_CREATING(1, 20),
    ACTIVITY_STATE_NORMAL(0, 0);

    private final int stateId;
    private final int timer;

    SnowStormActivityState(int stateId, int timer) {
        this.stateId = stateId;
        this.timer = timer;
    }

    public int getStateId() {
        return stateId;
    }

    public int getTimer() {
        return timer;
    }

    /**
     * Convert the amount of frames to real world time.
     *
     * @return the amount of frames
     */
    public int getTimeInMS() {
        return timer > 0 ? (timer / 5) * 300 : 0;
    }
}
