package net.h4bbo.kepler.messages.incoming.register;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.register.PASSWORD_APPROVED;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class APPROVE_PASSWORD implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        /*
        if (player.isLoggedIn()) {
            return;
        }
        */

        String username = reader.readString();
        String password = reader.readString();

        int error_code = 0;

        if (username.equals(password)) {
            error_code = 5;
        } else if (password.length() < 6) {
            error_code = 1;
        } else if (password.length() > 10) {
            error_code = 2;
        }

        player.send(new PASSWORD_APPROVED(error_code));
    }
}
