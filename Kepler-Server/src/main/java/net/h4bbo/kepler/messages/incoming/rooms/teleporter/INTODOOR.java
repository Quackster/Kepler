package net.h4bbo.kepler.messages.incoming.rooms.teleporter;

import net.h4bbo.kepler.dao.mysql.ItemDao;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.item.base.ItemBehaviour;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
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
        player.getRoomUser().setAuthenticateTelporterId(item.getId());
        player.getRoomUser().walkTo(item.getPosition().getX(), item.getPosition().getY());
        //player.getRoomUser().setWalkingAllowed(false);
    }
}
