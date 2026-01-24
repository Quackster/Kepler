package net.h4bbo.kepler.game.games.snowstorm.objects;

import net.h4bbo.kepler.game.games.GameObject;
import net.h4bbo.kepler.game.games.enums.GameObjectType;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all SnowStorm game objects.
 * Provides common interface for frame movement and checksum synchronization.
 */
public abstract class SnowStormGameObject extends GameObject {
    private final List<Integer> syncValues = new ArrayList<>();

    public SnowStormGameObject(int id, GameObjectType gameObjectType) {
        super(id, gameObjectType);
    }

    /**
     * Access the reusable container for checksum/sync values.
     */
    protected final List<Integer> getSyncValuesContainer() {
        this.syncValues.clear();
        return this.syncValues;
    }

    /**
     * Process one frame of movement for this object.
     * Called during each game turn for physics/movement updates.
     */
    public abstract void calculateFrameMovement();

    /**
     * Get the values used for checksum calculation.
     * The returned list should contain all state values in the order
     * expected by the client for synchronization validation.
     *
     * @return list of integer values for checksum calculation
     */
    public abstract List<Integer> getChecksumValues();

    /**
     * Calculate this object's contribution to the game state checksum.
     * Uses weighted sum of checksum values.
     *
     * @return the checksum contribution
     */
    public int getChecksumContribution() {
        List<Integer> values = getChecksumValues();
        int sum = 0;
        for (int i = 0; i < values.size(); i++) {
            sum += values.get(i) * (i + 1);
        }
        return sum;
    }

    /**
     * Check if this object is still alive/active in the game.
     *
     * @return true if object should remain in the game
     */
    public abstract boolean isAlive();
}
