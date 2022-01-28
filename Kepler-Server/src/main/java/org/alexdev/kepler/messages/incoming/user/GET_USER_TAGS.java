package org.alexdev.kepler.messages.incoming.user;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.USER_OBJECT;
import org.alexdev.kepler.messages.outgoing.user.USER_TAGS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_USER_TAGS implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        player.getRoomUser().getRoom().send(new USER_TAGS(player.getDetails()));
    }
}
