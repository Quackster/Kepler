package org.alexdev.kepler.game.games.snowstorm.util;

import org.alexdev.kepler.game.pathfinder.Position;

public class SnowStormSpawn {
    private Position position;
    private int radius;
    private int minDistance;

    public SnowStormSpawn(int X, int Y, int radius, int minDistance) {
        this.position = new Position(X, Y);
        this.radius = radius;
        this.minDistance = minDistance;
    }

    public Position getPosition() {
        return position;
    }

    public int getRadius() {
        return radius;
    }

    public int getMinDistance() {
        return minDistance;
    }
}
