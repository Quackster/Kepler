package net.h4bbo.kepler.messages.incoming.handshake;

import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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
