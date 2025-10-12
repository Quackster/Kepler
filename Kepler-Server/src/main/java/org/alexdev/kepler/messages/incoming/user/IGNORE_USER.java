package org.alexdev.kepler.messages.incoming.user;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.dao.mysql.UsersMutesDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.IGNORE_USER_RESULT;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class IGNORE_USER implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        String username = reader.readString();

        if (player.getIgnoredList().contains(username)) {
            return;
        }

        int userId = PlayerDao.getId(username);
        if (userId == -1 || userId == player.getDetails().getId()) {
            player.send(new IGNORE_USER_RESULT(0));
            return;
        }

        UsersMutesDao.addMuted(player.getDetails().getId(), userId);

        player.getIgnoredList().add(username);
        player.send(new IGNORE_USER_RESULT(1));
    }
}
