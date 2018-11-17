package org.alexdev.kepler.messages.incoming.user;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.USER_OBJECT;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_INFO implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        player.send(new USER_OBJECT(player.getDetails()));
    }
}
