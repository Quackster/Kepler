package net.h4bbo.kepler.game.item.roller;

import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.room.Room;

public interface RollingAnalysis<T> {
    public Position canRoll(T rollingType, Item roller, Room room);
    public void doRoll(T rollingType, Item roller, Room room, Position fromPosition, Position nextPosition);
}
