package org.alexdev.kepler.messages.incoming.rooms;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.user.USER_OBJECTS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class G_USRS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        if (player.getRoomUser().getGamePlayer() != null && player.getRoomUser().getGamePlayer().isInGame()) {
            return; // Not needed for game arenas
        }

        Room room = player.getRoomUser().getRoom();

        player.send(new USER_OBJECTS(room.getEntities()));
        room.send(new USER_OBJECTS(player));
    }
}
