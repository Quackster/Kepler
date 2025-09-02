package net.h4bbo.kepler.messages.incoming.rooms.moderation;

import net.h4bbo.kepler.dao.mysql.RoomRightsDao;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class REMOVERIGHTS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (!room.isOwner(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            return;
        }

        Player target = PlayerManager.getInstance().getPlayerByName(reader.contents());

        if (target == null) {
            return;
        }

        Integer userId = target.getDetails().getId();

        if (!room.getRights().contains(userId)) {
            return;
        }

        room.getRights().remove(userId);
        room.refreshRights(target);

        RoomRightsDao.removeRights(target.getDetails(), room.getData());
    }
}
