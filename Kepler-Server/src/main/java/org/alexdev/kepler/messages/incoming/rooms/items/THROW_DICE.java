package org.alexdev.kepler.messages.incoming.rooms.items;

import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.room.tasks.DiceTask;
import org.alexdev.kepler.messages.outgoing.rooms.items.DICE_VALUE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;


public class THROW_DICE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        RoomEntity roomEntity = player.getRoomUser();
        Room room = roomEntity.getRoom();

        if (room == null) {
            return;
        }

        String contents = reader.contents();

        if (!StringUtils.isNumeric(contents)) {
            return;
        }

        int itemId = Integer.parseInt(contents);

        if (itemId < 0) {
            return;
        }

        Item item = room.getItemManager().getById(itemId);

        if (item == null || !item.hasBehaviour(ItemBehaviour.DICE)) {
            return;
        }

        // Return if dice is already being rolled
        if (item.getRequiresUpdate()) {
            return;
        }

        // Check if user is next to dice
        if (!roomEntity.getTile().touches(item.getTile())) {
            return;
        }

        // We reset the room timer here too as in casinos you might be in the same place for a while
        // And you don't want to get kicked while you're still actively rolling dices for people :)
        player.getRoomUser().getTimerManager().resetRoomTimer();

        room.send(new DICE_VALUE(itemId, true, 0));

        // Send spinning animation to room
        item.setCustomData("-1");
        item.updateStatus();

        item.setRequiresUpdate(true);

        GameScheduler.getInstance().getSchedulerService().schedule(new DiceTask(item), 2, TimeUnit.SECONDS);
    }
}