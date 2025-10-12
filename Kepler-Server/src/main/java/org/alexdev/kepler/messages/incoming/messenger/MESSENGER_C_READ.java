package org.alexdev.kepler.messages.incoming.messenger;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class MESSENGER_C_READ implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        int messageId = Integer.parseInt(reader.readString());

        // Mark campaign message as read
    }
}
