package org.alexdev.kepler.game.item.triggers;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.triggers.GenericTrigger;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.room.public_rooms.PoolHandler;

public class PoolEnterTrigger extends GenericTrigger {

    @Override
    public void onEntityStep(Entity entity, RoomEntity roomEntity, Item item, Position oldPosition, Object... customArgs) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        if (entity.getRoomUser().containsStatus(StatusType.SWIM)) {
            return;
        }

        // Don't handle step event from RoomUser when changing paths
        if (customArgs.length > 0) {
            return;
        }

        Position warp = null;
        Position goal = null;

        if (item.getPosition().getX() == 20 && item.getPosition().getY() == 28) {
            warp = new Position(21, 28);
            goal = new Position(22, 28);
        }

        if (item.getPosition().getX() == 17 && item.getPosition().getY() == 21) {
            warp = new Position(16, 22);
            goal = new Position(16, 23);
        }

        if (item.getPosition().getX() == 31 && item.getPosition().getY() == 10) {
            warp = new Position(30, 11);
            goal = new Position(30, 12);
        }

        if ((item.getPosition().getX() == 11 && item.getPosition().getY() == 11) ||
                item.getPosition().getX() == 11 && item.getPosition().getY() == 10) {
            warp = new Position(12, 11);
            goal = new Position(13, 12);
        }

        if (warp != null) {
            PoolHandler.warpSwim(item, entity, warp, goal, false);
        }
    }

    @Override
    public void onEntityStop(Entity entity, RoomEntity roomEntity, Item item, Object... customArgs) {

    }

    @Override
    public void onEntityLeave(Entity entity, RoomEntity roomEntity, Item item, Object... customArgs) {

    }
}
