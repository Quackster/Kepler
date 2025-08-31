package org.alexdev.kepler.messages.incoming.rooms.user;

import org.alexdev.kepler.dao.mysql.TagDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.user.TAG_LIST;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_USER_TAGS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        Player p = PlayerManager.getInstance().getPlayerById(reader.readInt());

        if (p == null) {
            return;
        }

        player.send(new TAG_LIST(p.getDetails().getId(), TagDao.getUserTags(p.getDetails().getId())));
    }
}
