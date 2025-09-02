package net.h4bbo.kepler.messages.incoming.rooms.user;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class WAVE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        player.getRoomUser().wave();
        player.getRoomUser().getTimerManager().resetRoomTimer();
    }

}
