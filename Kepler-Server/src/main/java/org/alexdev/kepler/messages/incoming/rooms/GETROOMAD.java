package org.alexdev.kepler.messages.incoming.rooms;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.rooms.ROOM_AD;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GETROOMAD implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if(player.getRoomUser().getRoom().getId() == 1) {
            player.send(new ROOM_AD("http://dcr.webbanditten.dk/shared/ads/test", "http://google.com/"));
        } else {
            player.send(new ROOM_AD());
        }
    }
}
