package org.alexdev.kepler.messages.incoming.user;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.DateUtil;

public class PONG implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {

        long lastPing = player.getLastPing();
        long now = DateUtil.getCurrentTimeSeconds();

        long timeOnlineSinceLastPing = now - lastPing;
        PlayerDao.incrementOnlineTime(player.getDetails().getId(), timeOnlineSinceLastPing);
        player.setLastPing(now);


        // Nice pong :^)
        player.setPingOK(true);
    }
}
