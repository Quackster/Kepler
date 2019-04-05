package org.alexdev.kepler.messages.incoming.register;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.register.AGE_CHECK_RESULT;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class AGE_CHECK implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        player.send(new AGE_CHECK_RESULT());
    }
}
