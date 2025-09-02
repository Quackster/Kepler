package net.h4bbo.kepler.messages.incoming.rooms.teleporter;

import net.h4bbo.kepler.dao.mysql.ItemDao;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.RoomManager;
import net.h4bbo.kepler.messages.outgoing.rooms.OPEN_CONNECTION;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class GOVIADOOR implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (player.getRoomUser().getAuthenticateTelporterId() == -1) {
            return;
        }

        String[] data = reader.contents().split("/");

        int roomId = Integer.parseInt(data[0]);
        int itemId = Integer.parseInt(data[1]);

        Item linkedTeleporter = ItemDao.getItem(itemId);
        Room target = RoomManager.getInstance().getRoomById(roomId);

        if (linkedTeleporter != null && target != null) {
            // if (linkedTeleporter.getRoomId() != room.getId()) {
            player.getRoomUser().setAuthenticateId(roomId);
            player.send(new OPEN_CONNECTION());
            //} else {
                /*player.getRoomUser().setPosition(linkedTeleporter.getPosition().copy());
                room.send(new USER_STATUSES(List.of(player)));

                linkedTeleporter = room.getItemManager().getById(linkedTeleporter.getId());
                linkedTeleporter.setCustomData("TRUE");
                //linkedTeleporter.updateStatus();

                Position front = linkedTeleporter.getPosition().getSquareInFront();

                player.getRoomUser().walkTo(front.getX(), front.getY());
                player.getRoomUser().setAuthenticateTelporterId(-1);

                room.send(new BROADCAST_TELEPORTER(linkedTeleporter, player.getDetails().getName(), false));*/
            // }
        } else {
            player.getRoomUser().setAuthenticateTelporterId(-1);
        }
    }
}
