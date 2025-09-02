package net.h4bbo.kepler.messages.incoming.navigator;

import net.h4bbo.kepler.dao.mysql.RoomDao;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.RoomManager;
import net.h4bbo.kepler.messages.outgoing.navigator.RECOMMENDED_ROOM_LIST;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.util.List;

public class RECOMMENDED_ROOMS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        int roomLimit = 3;

        List<Room> roomList = RoomManager.getInstance().replaceQueryRooms(RoomDao.getRecommendedRooms(roomLimit, 0));

        RoomManager.getInstance().sortRooms(roomList);
        RoomManager.getInstance().ratingSantiyCheck(roomList);

        if (roomList.size() < roomLimit) {
            //int difference = roomLimit - roomList.size();

            for (Room room : RoomManager.getInstance().replaceQueryRooms(RoomDao.getHighestRatedRooms(roomLimit, 0))) {
                if (roomList.size() == roomLimit) {
                    break;
                }

                roomList.add(room);
            }
        }

        player.send(new RECOMMENDED_ROOM_LIST(player, roomList));
    }
}
