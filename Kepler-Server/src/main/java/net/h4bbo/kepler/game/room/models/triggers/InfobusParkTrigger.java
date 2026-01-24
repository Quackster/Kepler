package net.h4bbo.kepler.game.room.models.triggers;

import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.infobus.InfobusManager;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.triggers.GenericTrigger;
import net.h4bbo.kepler.messages.outgoing.infobus.BUS_DOOR;
import net.h4bbo.kepler.messages.types.MessageComposer;

import java.util.ArrayList;
import java.util.List;

public class InfobusParkTrigger extends GenericTrigger {
    @Override
    public void onRoomEntry(Entity entity, Room room, boolean firstEntry, Object... customArgs) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;
        player.send(new BUS_DOOR(InfobusManager.getInstance().isDoorOpen()));

        /*
        List<MessageComposer> messageComposers = new ArrayList<>();
        player.getRoomUser().getPacketQueueAfterRoomLeave().drainTo(messageComposers);

        for (var composer : messageComposers) {
            player.send(composer);
        }*/
    }

    @Override
    public void onRoomLeave(Entity entity, Room room, Object... customArgs) {

    }
}
