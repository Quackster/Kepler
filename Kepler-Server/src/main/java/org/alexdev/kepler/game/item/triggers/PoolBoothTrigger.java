package org.alexdev.kepler.game.item.triggers;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.triggers.GenericTrigger;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.rooms.pool.OPEN_UIMAKOPPI;

public class PoolBoothTrigger extends GenericTrigger {

    @Override
    public void onEntityStop(Entity entity, RoomEntity roomEntity, Item item, Object... customArgs) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player)entity;

        player.getRoomUser().setWalkingAllowed(false);
        player.getRoomUser().getTimerManager().resetRoomTimer(120); // Only allow 120 seconds when changing clothes, to stop someone from just afking in the booth for 15 minutes.
        player.send(new OPEN_UIMAKOPPI());

        item.showProgram("close");
    }

    @Override
    public void onEntityLeave(Entity entity, RoomEntity roomEntity, Item item, Object... customArgs) {
        if (roomEntity.isWalking()) {
            return;
        }

        item.showProgram("open");
    }
}
