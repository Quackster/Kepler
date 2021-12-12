package org.alexdev.kepler.messages.incoming.rooms.teleporter;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.apache.commons.lang3.StringUtils;

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

        Item item = room.getItemManager().getByVirtualId(Integer.parseInt(contents));

        if (item == null || !item.hasBehaviour(ItemBehaviour.TELEPORTER)) {
            return;
        }

        if (player.getRoomUser().getAuthenticateTelporterId() != null) {
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
        player.getRoomUser().setAuthenticateTelporterId(item.getDatabaseId());
        player.getRoomUser().walkTo(item.getPosition().getX(), item.getPosition().getY());
        //player.getRoomUser().setWalkingAllowed(false);
    }
}
