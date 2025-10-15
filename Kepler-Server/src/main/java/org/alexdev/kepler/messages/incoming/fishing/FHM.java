package org.alexdev.kepler.messages.incoming.fishing;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class FHM implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        final String key = reader.readString();

        player.getLogger().info("FHM key={}", key);
    }
}
