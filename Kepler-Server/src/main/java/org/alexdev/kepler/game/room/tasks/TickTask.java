package org.alexdev.kepler.game.room.tasks;

import org.alexdev.kepler.util.DateUtil;

public abstract class TickTask {
    private long timeUntilNextTick;

    public abstract void tick();

    public void setTimeUntilNextTick(int secs) {
        this.timeUntilNextTick = DateUtil.getCurrentTimeSeconds() + secs;
    }

    public boolean isTickRunnable() {
        return DateUtil.getCurrentTimeSeconds() > timeUntilNextTick;
    }
}
