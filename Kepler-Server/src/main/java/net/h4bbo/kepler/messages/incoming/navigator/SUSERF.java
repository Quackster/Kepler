package net.h4bbo.kepler.messages.incoming.navigator;

import net.h4bbo.kepler.dao.mysql.RoomDao;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.RoomManager;
import net.h4bbo.kepler.messages.outgoing.navigator.FLAT_RESULTS;
import net.h4bbo.kepler.messages.outgoing.navigator.NOFLATSFORUSER;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.util.List;

public class SUSERF implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        List<Room> roomList = RoomManager.getInstance().replaceQueryRooms(RoomDao.getRoomsByUserId(player.getDetails().getId()));

        if (roomList.size() > 0) {
            RoomManager.getInstance().sortRooms(roomList);
            RoomManager.getInstance().ratingSantiyCheck(roomList);

            player.send(new FLAT_RESULTS(roomList));
        } else {
            player.send(new NOFLATSFORUSER(player.getDetails().getName()));
        }
    }
}
