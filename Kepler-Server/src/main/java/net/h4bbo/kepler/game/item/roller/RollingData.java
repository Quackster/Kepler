package net.h4bbo.kepler.game.item.roller;

import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.pathfinder.Position;

public class RollingData {
    private Item roller;
    private Item item;
    private Entity entity;
    private Position fromPosition;
    private Position nextPosition;
    private double displayHeight;
    private double heightUpdate;

    public RollingData(Entity entity, Item roller, Position fromPosition, Position nextPosition) {
        this.entity = entity;
        this.roller = roller;
        this.heightUpdate = -1;
        this.fromPosition = fromPosition;
        this.nextPosition = nextPosition;
    }

    public RollingData(Item item, Item roller, Position fromPosition, Position nextPosition) {
        this.item = item;
        this.roller = roller;
        this.heightUpdate = -1;
        this.fromPosition = fromPosition;
        this.nextPosition = nextPosition;
    }

    public Item getItem() {
        return item;
    }

    public Item getRoller() {
        return roller;
    }

    public double getHeightUpdate() {
        return heightUpdate;
    }

    public void setHeightUpdate(double heightUpdate) {
        this.heightUpdate = heightUpdate;
    }

    public Position getNextPosition() {
        return nextPosition;
    }

    public Position getFromPosition() {
        return fromPosition;
    }

    public double getDisplayHeight() {
        return displayHeight;
    }

    public void setDisplayHeight(double displayHeight) {
        this.displayHeight = displayHeight;
    }

    public Entity getEntity() {
        return entity;
    }
}
