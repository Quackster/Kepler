package org.alexdev.kepler.messages.incoming.rooms.settings;

import org.alexdev.kepler.dao.mysql.RoomDao;
import org.alexdev.kepler.game.navigator.NavigatorCategory;
import org.alexdev.kepler.game.navigator.NavigatorManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class SETFLATCAT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        int roomId = reader.readInt();
        int categoryId = reader.readInt();

        NavigatorCategory category = NavigatorManager.getInstance().getCategoryById(categoryId);

        if (category == null) {
            return;
        }

        if (category.getMinimumRoleSetFlat().getRankId() > player.getDetails().getRank().getRankId()) {
            return;
        }

        Room room = RoomManager.getInstance().getRoomById(roomId);

        if (room == null) {
            return;
        }

        if (!room.isOwner(player.getDetails().getId())) {
            return;
        }

        room.getData().setCategoryId(category.getId());
        RoomDao.save(room);
    }
}
