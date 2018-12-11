package org.alexdev.kepler.game.room.models.triggers;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.triggers.GenericTrigger;

public class HabboLidoTrigger extends GenericTrigger {
    @Override
    public void onRoomEntry(Entity entity, Room room, Object... customArgs) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player)entity;

        if (player.getRoomUser().getPosition().getZ() == 1.0) { // User entered room from the other pool
            player.getRoomUser().setStatus(StatusType.SWIM, "");
            player.getRoomUser().setNeedsUpdate(true);
        }
    }

    @Override
    public void onRoomLeave(Entity entity, Room room, Object... customArgs)  {

    }
}
