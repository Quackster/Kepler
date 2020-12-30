package org.alexdev.kepler.game.games.snowstorm.mapping;

import org.alexdev.kepler.game.games.snowstorm.objects.SnowballObject;

import java.util.Comparator;
import java.util.List;

public class SnowStormTile {
    private int X;
    private int Y;
    private List<SnowStormItem> items;
    private SnowStormItem highestItem;
    private boolean isBlocked;

    public SnowStormTile(int x, int y, boolean isBlocked, List<SnowStormItem> items) {
        this.X = x;
        this.Y = y;
        this.isBlocked = isBlocked;

        this.items = items;
        this.items.sort(Comparator.comparingDouble((SnowStormItem item) -> item.getPosition().getZ()));

        for (var item : this.items) {
            if ((this.highestItem == null) || (item.getPosition().getZ() > this.highestItem.getPosition().getZ())) {
                this.highestItem = item;
            }
        }
    }

    public boolean isWalkable() {
        if (this.isBlocked) {
            return false;
        }

        if (this.highestItem != null) {
            return false;
        }

        return true;
    }

    public SnowStormItem getHighestItem() {
        return highestItem;
    }

    public List<SnowStormItem> getItems() {
        return items;
    }

    public boolean isHeightBlocking(SnowballObject.SnowballTrajectory trajectory) {
        if (this.highestItem == null) {
            return false;
        }

        if (trajectory == SnowballObject.SnowballTrajectory.LONG_TRAJECTORY) {
            return false;
        }

        if (trajectory == SnowballObject.SnowballTrajectory.SHORT_TRAJECTORY) {
            return this.highestItem.getHeight() > 1;
        }

        if (trajectory == SnowballObject.SnowballTrajectory.QUICK_THROW) {
            return this.highestItem.getHeight() > 0;
        }

        return false;
    }
}
