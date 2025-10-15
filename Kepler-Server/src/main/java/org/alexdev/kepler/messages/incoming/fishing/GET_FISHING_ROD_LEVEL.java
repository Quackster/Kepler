package org.alexdev.kepler.messages.incoming.fishing;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_FISHING_ROD_LEVEL implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {

    }
}
