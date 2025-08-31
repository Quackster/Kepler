package org.alexdev.kepler.game.room.models.triggers;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.tasks.SpaceCafeTask;
import org.alexdev.kepler.game.triggers.GenericTrigger;

import java.util.concurrent.TimeUnit;

public class SpaceCafeTrigger extends GenericTrigger {
    @Override
    public void onRoomEntry(Entity entity, Room room, boolean firstEntry, Object... customArgs) {
        if (!firstEntry) {
            return;
        }

        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        room.getTaskManager().scheduleTask("SpaceCafeTask", new SpaceCafeTask(room), 0, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onRoomLeave(Entity entity, Room room, Object... customArgs) {

    }
}
