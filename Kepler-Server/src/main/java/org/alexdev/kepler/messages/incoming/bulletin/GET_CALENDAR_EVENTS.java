package org.alexdev.kepler.messages.incoming.bulletin;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_CALENDAR_EVENTS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        // TODO: Handle.
    }
}
