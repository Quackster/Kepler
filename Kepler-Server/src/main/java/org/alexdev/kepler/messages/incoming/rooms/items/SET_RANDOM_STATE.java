package org.alexdev.kepler.messages.incoming.rooms.items;

import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.room.tasks.SetRandomStateTask;
import org.alexdev.kepler.messages.outgoing.rooms.items.STUFFDATAUPDATE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.concurrent.TimeUnit;

public class SET_RANDOM_STATE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        RoomEntity roomEntity = player.getRoomUser();
        Room room = roomEntity.getRoom();

        if (room == null) {
            return;
        }

        int itemId = reader.readInt();

        if (itemId < 0) {
            return;
        }

        Item item = room.getItemManager().getById(itemId);

        if (item == null || !item.hasBehaviour(ItemBehaviour.SET_RANDOM_STATE)) {
            return;
        }

        if (item.hasBehaviour(ItemBehaviour.REQUIRES_RIGHTS_FOR_INTERACTION)
                && !room.hasRights(player.getDetails().getId())
                && !player.hasFuse(Fuseright.MOD)) {
            return;
        }

        // Return if state is already being rolled
        if (item.getRequiresUpdate()) {
            return;
        }


        // We reset the room timer here too as in casinos you might be in the same place for a while
        // And you don't want to get kicked while you're still actively rolling dices for people :)
        player.getRoomUser().getTimerManager().resetRoomTimer();


        // Send spinning animation to room
        item.setCustomData("123456789");
        room.send(new STUFFDATAUPDATE(item));

        item.updateStatus();

        item.setRequiresUpdate(true);

        GameScheduler.getInstance().getService().schedule(new SetRandomStateTask(item), 5, TimeUnit.SECONDS);
    }
}
