package org.alexdev.kepler.game.room.models.triggers;

import org.alexdev.kepler.game.infobus.InfobusManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.triggers.GenericTrigger;
//import org.alexdev.kepler.messages.outgoing.rooms.POLL_QUESTION;

public class InfobusPollTrigger extends GenericTrigger {
    @Override
    public void onRoomEntry(Entity entity, Room room, boolean firstEntry, Object... customArgs) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;
        InfobusManager.getInstance().addPlayer(player.getDetails().getId());

        //player.send(new POLL_QUESTION("How about I fuck your shit up?", new String[] { "Yes please", "No please", "How about both?"}));
    }

    @Override
    public void onRoomLeave(Entity entity, Room room, Object... customArgs) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;
        InfobusManager.getInstance().removePlayer(player.getDetails().getId());
    }
}
