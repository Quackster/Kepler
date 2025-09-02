package net.h4bbo.kepler.messages.incoming.register;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.register.DATE;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
import net.h4bbo.kepler.util.DateUtil;

public class GDATE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        player.send(new DATE(DateUtil.getShortDate().replace("-", ".")));
    }
}
