package org.alexdev.kepler.messages.incoming.handshake;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.LOCALISED_ERROR;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class SSO implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.isLoggedIn()) {
            return;
        }

        String ticket = reader.readString();

        if (!PlayerDao.loginTicket(player, ticket)) {
            //player.send(new LOCALISED_ERROR("Incorrect SSO ticket"));
            player.kickFromServer();
        } else {
            player.login();
        }
    }
}
