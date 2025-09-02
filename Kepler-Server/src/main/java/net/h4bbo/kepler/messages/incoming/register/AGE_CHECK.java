package net.h4bbo.kepler.messages.incoming.register;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.register.AGE_CHECK_RESULT;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class AGE_CHECK implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        player.send(new AGE_CHECK_RESULT());
    }
}
