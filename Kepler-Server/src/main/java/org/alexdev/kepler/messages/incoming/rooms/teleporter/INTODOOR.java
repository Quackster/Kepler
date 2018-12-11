package org.alexdev.kepler.messages.incoming.rooms.teleporter;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.messages.outgoing.rooms.items.BROADCAST_TELEPORTER;
import org.alexdev.kepler.messages.outgoing.rooms.items.TELEPORTER_INIT;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

public class INTODOOR implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        String contents = reader.contents();

        if (!StringUtils.isNumeric(contents)) {
            return;
        }

        Item item = room.getItemManager().getById(Integer.parseInt(contents));

        if (item == null || !item.hasBehaviour(ItemBehaviour.TELEPORTER)) {
            return;
        }

        if (player.getRoomUser().getAuthenticateTelporterId() != -1) {
            return;
        }


        Item linkedTeleporter = ItemDao.getItem(item.getTeleporterId());

        if (linkedTeleporter == null) {
            return;
        }

        if (!item.getPosition().getSquareInFront().equals(player.getRoomUser().getPosition())) {
            return;
        }

       // player.getRoomUser().setAuthenticateTelporterId(item.getId());
        player.getRoomUser().setAuthenticateTelporterId(-1);
        player.getRoomUser().walkTo(item.getPosition().getX(), item.getPosition().getY());
        //player.getRoomUser().setWalkingAllowed(false);
    }
}
