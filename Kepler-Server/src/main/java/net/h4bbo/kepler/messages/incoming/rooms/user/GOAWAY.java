package net.h4bbo.kepler.messages.incoming.rooms.user;

import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.handlers.walkways.WalkwaysManager;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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