package org.alexdev.kepler.game.room.models.triggers;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.infobus.Infobus;
import org.alexdev.kepler.game.infobus.InfobusManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.triggers.GenericTrigger;
import org.alexdev.kepler.messages.outgoing.rooms.items.SHOWPROGRAM;

import java.util.concurrent.TimeUnit;

public class InfobusParkTrigger extends GenericTrigger {
    @Override
    public void onRoomEntry(Entity entity, Room room, boolean firstEntry, Object... customArgs) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        // Spawn new infobus
        Infobus infobus = new Infobus(room);
        String infobusTaskName = InfobusManager.getInstance().getName();
        if(!room.getTaskManager().hasTask(infobusTaskName)) {
            infobus.openDoor();
            room.getTaskManager().scheduleTask(infobusTaskName, infobus, TimeUnit.SECONDS.toMillis(3), 500, TimeUnit.MILLISECONDS);
        };

    }

    @Override
    public void onRoomLeave(Entity entity, Room room, Object... customArgs) {

    }
}
