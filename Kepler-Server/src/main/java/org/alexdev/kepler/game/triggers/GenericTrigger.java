package org.alexdev.kepler.game.triggers;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.entities.RoomEntity;

public abstract class GenericTrigger {
    public void onRoomEntry(Entity entity, Room room, Object... customArgs) { }
    public void onRoomLeave(Entity entity, Room room, Object... customArgs) { }
    public void onEntityStep(Entity entity, RoomEntity roomEntity, Item item, Position oldPosition, Object... customArgs) { }
    public void onEntityStop(Entity entity, RoomEntity roomEntity, Item item, Object... customArgs) { }
    public void onEntityLeave(Entity entity, RoomEntity roomEntity, Item item, Object... customArgs) { }
}
