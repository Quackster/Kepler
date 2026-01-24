package net.h4bbo.kepler.messages.incoming.handshake;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.handshake.AVAILABLE_SETS;
import net.h4bbo.kepler.messages.outgoing.handshake.SESSION_PARAMETERS;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
import net.h4bbo.kepler.util.config.GameConfiguration;

public class GENERATEKEY implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.isLoggedIn()) {
            return;
        }

        player.send(new SESSION_PARAMETERS(player.getDetails()));

        //if (player.getVersion() <= 17) {
        player.send(new AVAILABLE_SETS("[" + GameConfiguration.getInstance().getString("users.figure.parts.default") + "]"));
        //}
    }
}
