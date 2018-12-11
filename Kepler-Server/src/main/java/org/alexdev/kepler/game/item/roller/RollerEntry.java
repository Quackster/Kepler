package org.alexdev.kepler.game.item.roller;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.item.Item;

import java.util.ArrayList;
import java.util.List;

public class RollerEntry {
    private Item roller;
    private List<Item> rollingItems;
    private Entity rollingEntity;

    public RollerEntry(Item roller) {
        this.roller = roller;
        this.rollingItems = new ArrayList<>();
        this.rollingEntity = null;
    }

    public Item getRoller() {
        return roller;
    }

    public List<Item> getRollingItems() {
        return rollingItems;
    }

    public Entity getRollingEntity() {
        return rollingEntity;
    }

    public void setRollingEntity(Entity rollingEntity) {
        this.rollingEntity = rollingEntity;
    }
}
