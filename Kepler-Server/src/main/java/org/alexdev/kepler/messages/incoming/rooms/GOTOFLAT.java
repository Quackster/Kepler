package org.alexdev.kepler.messages.incoming.rooms;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GOTOFLAT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        int roomId = Integer.parseInt(reader.contents());

        if (player.getRoomUser().getAuthenticateId() != roomId) {
            return;
        }

        Room room = RoomManager.getInstance().getRoomById(roomId);

        if (room == null) {
            return;
        }

        room.getEntityManager().enterRoom(player, null);
    }
}
