package net.h4bbo.kepler.messages.incoming.handshake;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.handshake.CRYPTO_PARAMETERS;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class INIT_CRYPTO implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.isLoggedIn()) {
            return;
        }

        player.send(new CRYPTO_PARAMETERS());
    }
}
