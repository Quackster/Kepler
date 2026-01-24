package net.h4bbo.kepler.messages.incoming.inventory;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class GETSTRIP implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        String stripView = reader.contents();
        player.getInventory().getView(stripView);
    }
}
