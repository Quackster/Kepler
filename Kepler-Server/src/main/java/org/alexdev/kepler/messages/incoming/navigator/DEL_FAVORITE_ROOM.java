package org.alexdev.kepler.messages.incoming.navigator;

import org.alexdev.kepler.dao.mysql.RoomFavouritesDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class DEL_FAVORITE_ROOM implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        int roomType = reader.readInt();
        int roomId = reader.readInt();

        if (roomType == 1) {
            roomId = (roomId - RoomManager.PUBLIC_ROOM_OFFSET);
        }

        RoomFavouritesDao.removeFavouriteRoom(player.getDetails().getId(), roomId);
    }
}
