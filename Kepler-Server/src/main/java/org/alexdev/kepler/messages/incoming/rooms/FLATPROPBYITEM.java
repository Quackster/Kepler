package org.alexdev.kepler.messages.incoming.rooms;

import org.alexdev.kepler.dao.mysql.RoomDao;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.FLATPROPERTY;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.sql.SQLException;

public class FLATPROPBYITEM implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws SQLException {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (!room.isOwner(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            return;
        }

        String contents = reader.contents();
        String property = contents.split("/")[0];

        int itemId = Integer.parseInt(contents.split("/")[1]);

        Item item = player.getInventory().getItem(itemId);

        if (item == null) {
            return;
        }

        //int value = Integer.parseInt(item.getCustomData());

        if (property.equals("wallpaper")) {
            room.getData().setWallpaper(Integer.parseInt(item.getCustomData()));
        }

        if (property.equals("floor")) {
            room.getData().setFloor(Integer.parseInt(item.getCustomData()));
        }

        if (property.equals("landscape")) {
            room.getData().setLandscape(item.getCustomData());
        }

        item.delete();
        RoomDao.saveDecorations(room);

        room.send(new FLATPROPERTY(property, item.getCustomData()));
        player.getInventory().getItems().remove(item);
    }
}
