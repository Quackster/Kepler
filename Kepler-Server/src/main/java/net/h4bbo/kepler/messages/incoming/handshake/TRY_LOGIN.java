package net.h4bbo.kepler.messages.incoming.handshake;

import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.alert.LOCALISED_ERROR;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
import net.h4bbo.kepler.util.StringUtil;

public class TRY_LOGIN implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.isLoggedIn()) {
            return;
        }
        
        String username = StringUtil.filterInput(reader.readString(), true);
        String password = StringUtil.filterInput(reader.readString(), true);

        if (!PlayerDao.login(player.getDetails(), username, password)) {
            player.send(new LOCALISED_ERROR("Login incorrect"));
        } else {
            player.login();
        }
    }
}
