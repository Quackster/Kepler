package org.alexdev.kepler.game.games;

import org.alexdev.kepler.game.pathfinder.Position;

public abstract class GameTile {
    private Position position;
    private boolean isSpawnOccupied;

    public GameTile(Position position) {
        this.position = position;
    }

    /**
     * Get the position of this tile
     *
     * @return the position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Set whether this tile has been used as a spawn point
     *
     * @return true, if successful
     */
    public boolean isSpawnOccupied() {
        return isSpawnOccupied;
    }

    /**
     * Get whether this tile has been used as a spawn point
     *
     * @param spawnOccupied whether the spawn is occupied for spawning a player
     */
    public void setSpawnOccupied(boolean spawnOccupied) {
        isSpawnOccupied = spawnOccupied;
    }
}
