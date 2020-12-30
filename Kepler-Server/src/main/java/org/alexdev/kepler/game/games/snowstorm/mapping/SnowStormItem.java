package org.alexdev.kepler.game.games.snowstorm.mapping;

import org.alexdev.kepler.game.pathfinder.Position;

public class SnowStormItem {
    private final String itemId;
    private final String itemName;
    private final int x;
    private final int y;
    private final int z;
    private final int rotation;
    private final int height;

    public SnowStormItem(String itemId, String itemName, int x, int y, int z, int rotation, int height) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = rotation;
        this.height = height;
    }

    public boolean isSnowballMachine() {
        return this.itemName.equalsIgnoreCase("snowball_machine");
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getRotation() {
        return rotation;
    }

    public Position getPosition() {
        return new Position(this.x, this.y, this.z);
    }

    public int getHeight() {
        return height;
    }
}
