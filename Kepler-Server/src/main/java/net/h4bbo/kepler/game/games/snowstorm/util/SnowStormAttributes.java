package org.alexdev.kepler.game.games.snowstorm.util;

import org.alexdev.kepler.game.pathfinder.Position;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SnowStormAttributes {
    private volatile boolean isWalking;
    private volatile Position currentPosition;
    private volatile Position walkGoal;
    private volatile Position nextGoal;
    private volatile int[] goalWorldCoordinates;
    private final AtomicInteger snowballs;
    private final AtomicInteger health;
    private final AtomicInteger score;
    private volatile int rotation;
    private final AtomicLong lastThrow;
    private volatile long immunityExpiry;
    private volatile SnowStormActivityState activityState;
    private volatile int activityTimer;
    private final AtomicInteger healthToImplement;
    private volatile boolean isStunnedToImplement;
    private volatile Position gameObjectWorldLocation;
    private volatile int currentMachineId;

    public SnowStormAttributes() {
        this.snowballs = new AtomicInteger(0);
        this.health = new AtomicInteger(0);
        this.healthToImplement = new AtomicInteger(0);
        this.score = new AtomicInteger(0);
        this.lastThrow = new AtomicLong(0);
        this.immunityExpiry = 0;
        this.activityTimer = 0;
        this.isStunnedToImplement = false;
        this.activityState = SnowStormActivityState.ACTIVITY_STATE_NORMAL;
        this.currentMachineId = -1;
    }

    public boolean getStunnedToImplement() {
        return this.isStunnedToImplement;
    }

    public void setStunnedToImplement(boolean stunnedToImplement) {
        this.isStunnedToImplement = stunnedToImplement;
    }

    public AtomicInteger getHealthToImplement() {
        return this.healthToImplement;
    }

    public int getActivityTimer() {
        return activityTimer;
    }

    public void setActivityTimer(int activityTimer) {
        this.activityTimer = activityTimer;
    }

    public void decreaseActivityTimer() {
        if (this.activityTimer > 0) {
            this.activityTimer--;
        }
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
        if (this.activityState == null) {
            return false;
        }

        return this.activityState == SnowStormActivityState.ACTIVITY_STATE_NORMAL ||
                this.activityState == SnowStormActivityState.ACTIVITY_STATE_INVINCIBLE_AFTER_STUN;
    }

    public boolean isDamageable() {
        if (this.activityState == null) {
            return false;
        }

        return System.currentTimeMillis() > this.immunityExpiry &&
                this.activityState == SnowStormActivityState.ACTIVITY_STATE_NORMAL;
    }

    public void setActivityState(SnowStormActivityState activityState) {
        this.activityState = activityState;
    }

    public AtomicInteger getScore() {
        return score;
    }

    public void setScore(AtomicInteger score) {
        if (score != null) {
            this.score.set(score.get());
        }
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

    public Position getGameObjectWorldLocation() {
        return gameObjectWorldLocation;
    }

    public void setGameObjectWorldLocation(Position position) {
        this.gameObjectWorldLocation = position;
    }

    public int getCurrentMachineId() {
        return currentMachineId;
    }

    public void setCurrentMachineId(int currentMachineId) {
        this.currentMachineId = currentMachineId;
    }
}
