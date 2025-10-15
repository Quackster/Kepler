package org.alexdev.kepler.messages.incoming.fishing;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class PURCHASE_FISHING_PRODUCT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        final String code = reader.readString();

        Log.getErrorLogger().info("PURCHASE_FISHING_PRODUCT code={}", code);
    }
}
