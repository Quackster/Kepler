package org.alexdev.kepler.messages.incoming.rooms.settings;

import org.alexdev.kepler.dao.mysql.RoomDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.StringUtil;

public class UPDATEFLAT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        String[] data = reader.contents().split("/");

        int roomId = Integer.parseInt(data[0]);

        Room room = RoomManager.getInstance().getRoomById(roomId);

        if (room == null) {
            return;
        }

        if (!room.isOwner(player.getDetails().getId())) {
            return;
        }

        String roomName = StringUtil.filterInput(data[1], true);
        String accessType = StringUtil.filterInput(data[2], true);
        boolean showOwner = Integer.parseInt(data[3]) == 1;

        int accessTypeId = 0;

        if (accessType.equals("closed")) {
            accessTypeId = 1;
        }

        if (accessType.equals("password")) {
            accessTypeId = 2;
        }

        room.getData().setName(roomName);
        room.getData().setAccessType(accessTypeId);
        room.getData().setShowOwnerName(showOwner);
        RoomDao.save(room);
    }
}
