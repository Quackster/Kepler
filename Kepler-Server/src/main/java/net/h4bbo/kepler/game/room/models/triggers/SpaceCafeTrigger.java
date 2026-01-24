package net.h4bbo.kepler.game.room.models.triggers;

import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.tasks.SpaceCafeTask;
import net.h4bbo.kepler.game.triggers.GenericTrigger;

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
