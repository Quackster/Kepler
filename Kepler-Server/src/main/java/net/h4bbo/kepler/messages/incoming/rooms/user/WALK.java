package net.h4bbo.kepler.messages.incoming.rooms.user;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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
