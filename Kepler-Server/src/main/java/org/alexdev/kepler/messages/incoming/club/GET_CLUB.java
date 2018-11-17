package org.alexdev.kepler.messages.incoming.club;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_CLUB implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        player.refreshClub();
    }
}
