package net.h4bbo.kepler.messages.incoming.user;

import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.dao.mysql.UsersMutesDao;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.user.IGNORE_USER_RESULT;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class IGNORE_USER implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        String username = reader.readString();

        if (player.getIgnoredList().contains(username)) {
            return;
        }

        int userId = PlayerDao.getId(username);
        UsersMutesDao.addMuted(player.getDetails().getId(), userId);

        player.getIgnoredList().add(username);
        player.send(new IGNORE_USER_RESULT(1));
    }
}
