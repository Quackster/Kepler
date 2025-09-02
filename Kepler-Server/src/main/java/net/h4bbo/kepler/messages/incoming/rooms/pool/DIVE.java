package net.h4bbo.kepler.messages.incoming.rooms.pool;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.outgoing.rooms.pool.JUMPDATA;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class DIVE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        if (!player.getRoomUser().isDiving()) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        String divingHandle = reader.contents();
        room.send(new JUMPDATA(player.getRoomUser().getInstanceId(), divingHandle));
    }
}
