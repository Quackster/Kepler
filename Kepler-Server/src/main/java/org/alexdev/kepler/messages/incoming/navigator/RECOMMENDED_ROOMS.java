package org.alexdev.kepler.messages.incoming.navigator;

import org.alexdev.kepler.dao.mysql.RoomDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.outgoing.navigator.RECOMMENDED_ROOM_LIST;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.List;

public class RECOMMENDED_ROOMS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        List<Room> roomList = RoomManager.getInstance().replaceQueryRooms(RoomDao.getRandomRooms(3));
        RoomManager.getInstance().sortRooms(roomList);

        player.send(new RECOMMENDED_ROOM_LIST(player, roomList));
    }
}
