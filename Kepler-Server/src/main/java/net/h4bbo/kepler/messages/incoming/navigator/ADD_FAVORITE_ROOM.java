package net.h4bbo.kepler.messages.incoming.navigator;

import net.h4bbo.kepler.dao.mysql.RoomFavouritesDao;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.RoomManager;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.util.List;

public class ADD_FAVORITE_ROOM implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        int roomType = reader.readInt();
        int roomId = reader.readInt();

        if (roomType == 1) {
            roomId = (roomId - RoomManager.PUBLIC_ROOM_OFFSET);
        }

        Room room = RoomManager.getInstance().getRoomById(roomId);

        if (room == null) {
            return; // Room was null, ignore request
        }

        List<Room> favouritesList = RoomManager.getInstance().getFavouriteRooms(player.getDetails().getId());

        for (Room favroom : favouritesList) {
            if (favroom.getId() == roomId) {
                return; // Room already added, ignore request
            }
        }

        RoomFavouritesDao.addFavouriteRoom(player.getDetails().getId(), roomId);
    }
}
