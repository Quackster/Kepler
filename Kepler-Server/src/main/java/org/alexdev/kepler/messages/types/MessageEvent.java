package org.alexdev.kepler.messages.types;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public interface MessageEvent {
    
    /**
     * Handle the incoming client message.
     *
     * @param player the player
     * @param reader the reader
     */
    void handle(Player player, NettyRequest reader) throws Exception;
}
