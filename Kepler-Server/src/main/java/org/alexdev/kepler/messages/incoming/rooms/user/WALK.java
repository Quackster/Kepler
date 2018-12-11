package org.alexdev.kepler.messages.incoming.rooms.user;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class WALK implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (!player.getRoomUser().isWalkingAllowed()) {
            return;
        }

        int X = reader.readBase64();
        int Y = reader.readBase64();

        player.getRoomUser().walkTo(X, Y);
    }
}
