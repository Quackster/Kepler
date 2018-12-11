package org.alexdev.kepler.messages.incoming.rooms.moderation;

import org.alexdev.kepler.dao.mysql.RoomRightsDao;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class REMOVEALLRIGHTS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        int roomId = reader.readInt();

        Room room = RoomManager.getInstance().getRoomById(roomId);

        if (room == null) {
            return;
        }

        if (!room.isOwner(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            return;
        }

        room.getRights().clear();

        for (Player roomPlayer : room.getEntityManager().getPlayers()) {
            room.refreshRights(roomPlayer);
        }

        RoomRightsDao.deleteRoomRights(room.getData());
    }
}
