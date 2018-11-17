package org.alexdev.kepler.messages.incoming.rooms.pool;

import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.mapping.RoomTile;
import org.alexdev.kepler.messages.outgoing.rooms.items.SHOWPROGRAM;
import org.alexdev.kepler.messages.outgoing.rooms.user.USER_STATUSES;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.List;

public class SPLASH_POSITION implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        if (!player.getRoomUser().isDiving()) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (!room.getModel().getName().equals("pool_b")) {
            return;
        }

        Item currentItem = player.getRoomUser().getCurrentItem();

        if (!currentItem.getDefinition().getSprite().equals("poolLift")) {
            return;
        }

        String contents = "24,19";
        Position destination = new Position(24, 19);

        player.getRoomUser().setStatus(StatusType.SWIM, "");
        player.getRoomUser().warp(destination, true);

        room.send(new SHOWPROGRAM(new String[] { "BIGSPLASH", "POSITION", contents,}));

        player.getRoomUser().setWalkingAllowed(true);
        player.getRoomUser().setDiving(false);
        player.getRoomUser().walkTo(20, 19);

        currentItem.showProgram("open");
    }
}