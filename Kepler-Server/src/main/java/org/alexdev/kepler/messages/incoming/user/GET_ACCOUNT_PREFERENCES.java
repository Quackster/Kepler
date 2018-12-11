package org.alexdev.kepler.messages.incoming.user;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.ACCOUNT_PREFERENCES;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_ACCOUNT_PREFERENCES implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        player.send(new ACCOUNT_PREFERENCES(player.getDetails()));
    }
}