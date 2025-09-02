package net.h4bbo.kepler.messages.incoming.navigator;

import net.h4bbo.kepler.dao.mysql.RoomDao;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.RoomManager;
import net.h4bbo.kepler.messages.outgoing.navigator.FLAT_NORESULTS;
import net.h4bbo.kepler.messages.outgoing.navigator.NOFLATS;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.util.List;

public class SRCHF implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        String searchQuery = reader.contents();

        List<Room> roomList = RoomManager.getInstance().replaceQueryRooms(
                RoomDao.querySearchRooms(searchQuery));

        if (roomList.size() > 0) {
            RoomManager.getInstance().sortRooms(roomList);
            RoomManager.getInstance().ratingSantiyCheck(roomList);

            player.send(new FLAT_NORESULTS(roomList, player));
        } else {
            player.send(new NOFLATS());
        }
    }
}
