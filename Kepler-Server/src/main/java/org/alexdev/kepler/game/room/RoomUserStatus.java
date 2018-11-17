package org.alexdev.kepler.game.room;

import org.alexdev.kepler.game.room.enums.StatusType;

public class RoomUserStatus {
    private StatusType key;
    private String value;
    private StatusType action;

    private int secActionSwitch;
    private int secSwitchLifetime;
    private int lifetimeCountdown;

    private int actionCountdown;
    private int actionSwitchCountdown;

    public RoomUserStatus(StatusType status, String value) {
        this.key = status;
        this.value = value;

        this.secSwitchLifetime = -1;
        this.lifetimeCountdown = -1;
        this.secActionSwitch = -1;
        this.actionSwitchCountdown = -1;
        this.actionCountdown = -1;
    }

    public RoomUserStatus(StatusType status, String value, int secLifetime, StatusType action, int secActionSwitch, int secSwitchLifetime) {
        this.key = status;
        this.value = value;
        this.action = action;

        this.secActionSwitch = secActionSwitch;
        this.secSwitchLifetime = secSwitchLifetime;

        this.lifetimeCountdown = secLifetime;
        this.actionCountdown = secActionSwitch;
        this.actionSwitchCountdown = -1;
    }

    /**
     * Swap the key and action for timed statuses, used for drinking, etc.
     */
    public void swapKeyAction() {
        StatusType temp = this.key;
        this.key = this.action;
        this.action = temp;
    }

    public StatusType getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getSecActionSwitch() {
        return secActionSwitch;
    }


    public int getSecSwitchLifetime() {
        return secSwitchLifetime;
    }


    public int getLifetimeCountdown() {
        return lifetimeCountdown;
    }

    public void setLifetimeCountdown(int lifetimeCountdown) {
        this.lifetimeCountdown = lifetimeCountdown;
    }

    public int getActionCountdown() {
        return actionCountdown;
    }

    public void setActionCountdown(int actionCountdown) {
        this.actionCountdown = actionCountdown;
    }

    public int getActionSwitchCountdown() {
        return actionSwitchCountdown;
    }

    public void setActionSwitchCountdown(int actionSwitchCountdown) {
        this.actionSwitchCountdown = actionSwitchCountdown;
    }
}
