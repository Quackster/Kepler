package org.alexdev.kepler.messages.incoming.inventory;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GETSTRIP implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        String stripView = reader.contents();
        player.getInventory().getView(stripView);
    }
}
