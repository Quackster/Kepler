package org.alexdev.kepler.game.room.models.triggers;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.infobus.InfobusManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.triggers.GenericTrigger;

public class InfobusParkTrigger extends GenericTrigger {
    @Override
    public void onRoomEntry(Entity entity, Room room, boolean firstEntry, Object... customArgs) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;


        if(InfobusManager.getInstance().isDoorOpen()) {
            InfobusManager.getInstance().openDoor(room);
        };
       /* Infobus infobus = new Infobus(room);
        String infobusTaskName = InfobusManager.getInstance().getName();
        Room bus = RoomManager.getInstance().getRoomByModel("park_b");
        System.out.println(bus);
        if(!bus.getTaskManager().hasTask(infobusTaskName)) {
            infobus.openDoor();
            bus.getTaskManager().scheduleTask(infobusTaskName, infobus, TimeUnit.SECONDS.toMillis(3), 500, TimeUnit.MILLISECONDS);
        };*/

    }

    @Override
    public void onRoomLeave(Entity entity, Room room, Object... customArgs) {

    }
}
