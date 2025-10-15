package org.alexdev.kepler.game.fishing;

import org.alexdev.kepler.game.pathfinder.Position;

public class FishingArea {

    private final int amount;
    private final Position[] spots;

    public FishingArea(int amount, Position[] spots) {
        this.amount = amount;
        this.spots = spots;
    }

    public int getAmount() {
        return amount;
    }

    public Position[] getSpots() {
        return spots;
    }
}
