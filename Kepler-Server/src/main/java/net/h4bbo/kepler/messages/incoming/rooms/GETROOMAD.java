package net.h4bbo.kepler.messages.incoming.rooms;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.rooms.ROOM_AD;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class GETROOMAD implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        player.send(new ROOM_AD());
    }
}
