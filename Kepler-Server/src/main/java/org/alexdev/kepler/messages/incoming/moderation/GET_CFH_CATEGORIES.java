package org.alexdev.kepler.messages.incoming.moderation;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_CFH_CATEGORIES implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        // Respond CALL_FOR_HELP_CATEGORIES (550)
    }
}
