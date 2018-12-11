package org.alexdev.kepler.messages.incoming.rooms.user;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

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
