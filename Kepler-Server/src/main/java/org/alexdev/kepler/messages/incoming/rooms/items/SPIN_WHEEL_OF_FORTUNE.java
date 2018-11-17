package org.alexdev.kepler.messages.incoming.rooms.items;

import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.room.tasks.FortuneTask;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.concurrent.TimeUnit;


public class SPIN_WHEEL_OF_FORTUNE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        RoomEntity roomEntity = player.getRoomUser();
        Room room = roomEntity.getRoom();

        if (room == null) {
            return;
        }

        if (!room.hasRights(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            return;
        }

        int itemId = reader.readInt();

        if (itemId < 0) {
            return;
        }

        // Get item by ID
        Item item = room.getItemManager().getById(itemId);

        // Check if item exists and if it is a wheel of fortune
        if (item == null || !item.hasBehaviour(ItemBehaviour.WHEEL_OF_FORTUNE)) {
            return;
        }

        // Spin already being executed, return
        if (item.getRequiresUpdate()) {
            return;
        }

        // Send spinning animation to room
        item.setCustomData("-1");
        item.updateStatus();

        item.setRequiresUpdate(true);

        GameScheduler.getInstance().getSchedulerService().schedule(new FortuneTask(item), 4250, TimeUnit.MILLISECONDS);
    }
}