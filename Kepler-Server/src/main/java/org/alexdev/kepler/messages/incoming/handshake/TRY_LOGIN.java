package org.alexdev.kepler.messages.incoming.handshake;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.LOCALISED_ERROR;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.StringUtil;
import org.alexdev.kepler.util.config.ServerConfiguration;

public class TRY_LOGIN implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.isLoggedIn()) {
            return;
        }
        
        String username = StringUtil.filterInput(reader.readString(), true);
        String password = StringUtil.filterInput(reader.readString(), true);

        if (!PlayerDao.login(player.getDetails(), username, password,
                ServerConfiguration.getStringOrDefault("password.hashing.library", "argon2").equalsIgnoreCase("argon2"),
                ServerConfiguration.getStringOrDefault("password.hashing.library", "argon2").equalsIgnoreCase("bcrypt")
        )) {
            player.send(new LOCALISED_ERROR("Login incorrect"));
        } else {
            player.login();
        }
    }
}
