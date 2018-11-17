package org.alexdev.kepler.messages.incoming.navigator;

import org.alexdev.kepler.dao.mysql.RoomDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.outgoing.navigator.NOFLATSFORUSER;
import org.alexdev.kepler.messages.outgoing.navigator.FLAT_RESULTS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.List;

public class SUSERF implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        List<Room> roomList = RoomManager.getInstance().replaceQueryRooms(
                RoomDao.getRoomsByUserId(player.getDetails().getId()));

        if (roomList.size() > 0) {
            RoomManager.getInstance().sortRooms(roomList);
            player.send(new FLAT_RESULTS(roomList));
        } else {
            player.send(new NOFLATSFORUSER(player.getDetails().getName()));
        }
    }
}
