package net.h4bbo.kepler.messages.incoming.navigator;

import net.h4bbo.kepler.dao.mysql.RoomFavouritesDao;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.RoomManager;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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
