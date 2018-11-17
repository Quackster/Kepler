package org.alexdev.kepler.messages.incoming.user;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.LATENCY;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class TEST_LATENCY implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        int latency = reader.readInt();
        player.send(new LATENCY(latency));
    }
}
