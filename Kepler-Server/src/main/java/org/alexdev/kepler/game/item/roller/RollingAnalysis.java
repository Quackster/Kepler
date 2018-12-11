package org.alexdev.kepler.game.item.roller;

import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.room.Room;

public interface RollingAnalysis<T> {
    public Position canRoll(T rollingType, Item roller, Room room);
    public void doRoll(T rollingType, Item roller, Room room, Position fromPosition, Position nextPosition);
}
