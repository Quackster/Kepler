package org.alexdev.kepler.messages.incoming.register;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.register.DATE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.DateUtil;

public class GDATE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        player.send(new DATE(DateUtil.getShortDate().replace("-", ".")));
    }
}
