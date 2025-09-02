package net.h4bbo.kepler.game.triggers;

import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.entities.RoomEntity;

public abstract class GenericTrigger {
    public void onRoomEntry(Entity entity, Room room, boolean firstEntry, Object... customArgs) { }
    public void onItemPickup(Player player, Room room, Item item) { }
    public void onRoomLeave(Entity entity, Room room, Object... customArgs) { }
    public void onEntityStep(Entity entity, RoomEntity roomEntity, Item item, Position oldPosition) { }
    public void onEntityStop(Entity entity, RoomEntity roomEntity, Item item, boolean isRotation) { }
    public void onItemPlaced(Player player, Room room, Item item) { }
    public void onItemMoved(Player player, Room room, Item item, boolean isRotation, Position oldPosition, Item itemBelow, Item itemAbove) { }
    public void onEntityLeave(Entity entity, RoomEntity roomEntity, Item item) { }
}
