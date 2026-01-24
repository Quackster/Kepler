package net.h4bbo.kepler.messages.incoming.rooms.items;

import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.item.base.ItemBehaviour;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.entities.RoomEntity;
import net.h4bbo.kepler.messages.outgoing.rooms.items.DICE_VALUE;
import net.h4bbo.kepler.messages.outgoing.rooms.items.STUFFDATAUPDATE;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
import org.apache.commons.lang3.StringUtils;


public class DICE_OFF implements MessageEvent {
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

        if (!roomEntity.getTile().getPosition().touches(item.getTile().getPosition())) {
            return;
        }

        room.send(new DICE_VALUE(itemId, false, 0));
        room.send(new STUFFDATAUPDATE(item));

        item.setCustomData("0");
        item.save();
    }
}