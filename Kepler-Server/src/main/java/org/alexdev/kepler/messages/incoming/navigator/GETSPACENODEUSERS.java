package org.alexdev.kepler.messages.incoming.navigator;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.outgoing.navigator.NODESPACEUSERS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GETSPACENODEUSERS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        Room room = RoomManager.getInstance().getRoomById(reader.readInt() - RoomManager.PUBLIC_ROOM_OFFSET);

        if (room == null) {
            return;
        }

        var players = room.getEntityManager().getPlayers();

        if (room.getData().getChildRooms().size() > 0) {
            for (Room childRoom : room.getData().getChildRooms()) {
                players.addAll(childRoom.getEntityManager().getPlayers());
            }
        }

        player.send(new NODESPACEUSERS(players));
    }
}
