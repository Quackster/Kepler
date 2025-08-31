package org.alexdev.kepler.game.item.interactors.types.wobblesquabble;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.games.wobblesquabble.WobbleSquabbleManager;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.triggers.GenericTrigger;

public class WobbleSquabbleQueueTile extends GenericTrigger {
    @Override
    public void onEntityStop(Entity entity, RoomEntity roomEntity, Item item, boolean isRotation) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        if (roomEntity.getRoom().getTaskManager().hasTask(WobbleSquabbleManager.getInstance().getName())) {
            return;
        }

        roomEntity.walkTo(roomEntity.getPosition().getSquareInFront().getX(), roomEntity.getPosition().getSquareInFront().getY());
    }
}
