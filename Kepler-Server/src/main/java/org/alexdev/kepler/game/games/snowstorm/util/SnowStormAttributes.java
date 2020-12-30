package org.alexdev.kepler.game.games.snowstorm.util;

import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.pathfinder.Position;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SnowStormAttributes {
    private boolean isWalking;
    private Position currentPosition;
    private Position walkGoal;
    private Position nextGoal;
    private int[] goalWorldCoordinates;
    private AtomicInteger snowballs;
    private AtomicInteger health;
    private AtomicInteger score;
    private int rotation;
    private AtomicLong lastThrow;
    private long immunityExpiry;

    private SnowStormActivityState activityState;
    private long stateTime;

    public SnowStormAttributes() {
        this.snowballs = new AtomicInteger(0);
        this.health = new AtomicInteger(0);
        this.score = new AtomicInteger(0);
        this.lastThrow = new AtomicLong(0);
        this.immunityExpiry = 0;
    }

    public boolean isWalking() {
        return isWalking;
    }

    public void setWalking(boolean walking) {
        isWalking = walking;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Position getWalkGoal() {
        return walkGoal;
    }

    public void setWalkGoal(Position walkGoal) {
        this.walkGoal = walkGoal;
    }

    public Position getNextGoal() {
        return nextGoal;
    }

    public void setNextGoal(Position nextGoal) {
        this.nextGoal = nextGoal;
    }

    public int[] getGoalWorldCoordinates() {
        return goalWorldCoordinates;
    }

    public void setGoalWorldCoordinates(int[] goalWorldCoordinates) {
        this.goalWorldCoordinates = goalWorldCoordinates;
    }

    public AtomicInteger getSnowballs() {
        return snowballs;
    }

    public AtomicInteger getHealth() {
        return health;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public SnowStormActivityState getActivityState() {
        return activityState;
    }

    public boolean isWalkable() {
        return this.activityState == SnowStormActivityState.ACTIVITY_STATE_NORMAL ||
                this.activityState == SnowStormActivityState.ACTIVITY_STATE_INVINCIBLE_AFTER_STUN;
    }

    public boolean isDamageable() {
        return System.currentTimeMillis() > this.immunityExpiry && this.activityState == SnowStormActivityState.ACTIVITY_STATE_NORMAL;
    }

    public void setActivityState(SnowStormActivityState activityState, Runnable runnable) {
        this.activityState = activityState;
        this.stateTime = System.currentTimeMillis() + activityState.getTimeInMS();

        //System.out.println("Queued " + this.activityState.name() + " for " + activityState.getTimeInMS() + "ms");

        if (activityState != SnowStormActivityState.ACTIVITY_STATE_NORMAL) {
            GameScheduler.getInstance().getService().schedule(()-> {
                this.activityState = SnowStormActivityState.ACTIVITY_STATE_NORMAL;

                if (runnable != null)
                    runnable.run();
            }, this.activityState.getTimeInMS(), TimeUnit.MILLISECONDS);
        }
    }

    public void setActivityState(SnowStormActivityState activityState) {
        this.setActivityState(activityState, null);
    }

    public int getActivityTimer() {
        int timeRemaining = 0;
        long expireTime = this.stateTime + this.activityState.getTimeInMS();

        if (!(System.currentTimeMillis() > this.stateTime || this.activityState == SnowStormActivityState.ACTIVITY_STATE_NORMAL)) {
            timeRemaining = (int) (expireTime - System.currentTimeMillis());
            timeRemaining = (timeRemaining / 300)*5;
        }

        return timeRemaining;
     }

    public AtomicInteger getScore() {
        return score;
    }

    public void setScore(AtomicInteger score) {
        this.score = score;
    }

    public AtomicLong getLastThrow() {
        return lastThrow;
    }

    public long getImmunityExpiry() {
        return immunityExpiry;
    }

    public void setImmunityExpiry(long immunityExpiry) {
        this.immunityExpiry = immunityExpiry;
    }
}
