package net.h4bbo.kepler.messages.incoming.rooms.user;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class QUIT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        // Remove authentication values when user manually leaves
        player.getRoomUser().setAuthenticateTelporterId(-1);
        player.getRoomUser().setAuthenticateId(-1);

        player.getRoomUser().getRoom().getEntityManager().leaveRoom(player, false);
    }
}
