package org.alexdev.kepler.messages.incoming.rooms.user;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class WAVE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        player.getRoomUser().wave();
        player.getRoomUser().getTimerManager().resetRoomTimer();
    }

}
