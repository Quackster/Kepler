package net.h4bbo.kepler.messages.incoming.user;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.user.LATENCY;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class TEST_LATENCY implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        int latency = reader.readInt();
        player.send(new LATENCY(latency));
    }
}
