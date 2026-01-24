package net.h4bbo.kepler.messages.incoming.navigator;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.RoomManager;
import net.h4bbo.kepler.messages.outgoing.navigator.NODESPACEUSERS;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.util.ArrayList;
import java.util.List;

public class GETSPACENODEUSERS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        Room room = RoomManager.getInstance().getRoomById(reader.readInt() - RoomManager.PUBLIC_ROOM_OFFSET);

        if (room == null) {
            return;
        }

        var players = room.getEntityManager().getPlayers();

        List<Room> childRooms;

        if (room.isPublicRoom()) {
            childRooms = RoomManager.getInstance().getChildRooms(room);
        } else {
            childRooms = new ArrayList<>();
        }

        if (childRooms.size() > 0) {
            for (Room childRoom : childRooms) {
                players.addAll(childRoom.getEntityManager().getPlayers());
            }
        }

        player.send(new NODESPACEUSERS(players));
    }
}
