package org.alexdev.kepler.messages.incoming.rooms.user;

import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.public_rooms.walkways.WalkwaysManager;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GOAWAY implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        if (!player.getRoomUser().isWalkingAllowed()) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (room.isPublicRoom()) {
            Position doorPosition = room.getModel().getDoorLocation();

            if (WalkwaysManager.getInstance().getWalkway(room, doorPosition) != null) {
                return;
            }
        }

        player.getRoomUser().kick(true);
    }
}
