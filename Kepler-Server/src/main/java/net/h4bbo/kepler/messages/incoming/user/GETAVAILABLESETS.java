package net.h4bbo.kepler.messages.incoming.user;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.handshake.AVAILABLE_SETS;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
import net.h4bbo.kepler.util.config.GameConfiguration;

public class GETAVAILABLESETS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        // if (player.getVersion() <= 17) {
            if (player.getDetails().hasClubSubscription()) {
                player.send(new AVAILABLE_SETS("[" + GameConfiguration.getInstance().getString("users.figure.parts.club") + "]"));
            } else {
                player.send(new AVAILABLE_SETS("[" + GameConfiguration.getInstance().getString("users.figure.parts.default") + "]"));
            }
        // }
    }
}
