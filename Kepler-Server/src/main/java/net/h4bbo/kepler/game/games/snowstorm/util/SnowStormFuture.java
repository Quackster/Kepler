package net.h4bbo.kepler.game.games.snowstorm.util;

import net.h4bbo.kepler.game.games.GameObject;

public class SnowStormFuture {
    private int framesFuture;
    private int subTurn;
    private GameObject event;

    public SnowStormFuture(int framesFuture, int subTurn, GameObject event) {
        this.framesFuture = framesFuture;
        this.subTurn = subTurn;
        this.event = event;
    }

    public int getFramesFuture() {
        return framesFuture;
    }

    public int getSubTurn() {
        return subTurn;
    }

    public GameObject getEvent() {
        return event;
    }

    public void decrementFrame() {
        this.framesFuture--;
    }
}
