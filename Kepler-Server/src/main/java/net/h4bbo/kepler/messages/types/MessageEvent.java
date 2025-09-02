package net.h4bbo.kepler.messages.types;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public interface MessageEvent {
    
    /**
     * Handle the incoming client message.
     *
     * @param player the player
     * @param reader the reader
     */
    void handle(Player player, NettyRequest reader) throws Exception;
}
