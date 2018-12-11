package org.alexdev.kepler.messages.incoming.rooms;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.ITEMS;
import org.alexdev.kepler.messages.outgoing.rooms.OBJECTS_WORLD;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class G_ITEMS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        player.send(new ITEMS(room));
    }
}
