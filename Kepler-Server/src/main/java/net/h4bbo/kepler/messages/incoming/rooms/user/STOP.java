package net.h4bbo.kepler.messages.incoming.rooms.user;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.enums.StatusType;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class STOP implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        String stopWhat = reader.contents();

        if (stopWhat.equals("Dance")) {
            player.getRoomUser().removeStatus(StatusType.DANCE);
            player.getRoomUser().setNeedsUpdate(true);
        }

        if (stopWhat.equals("CarryItem")) {
            player.getRoomUser().removeStatus(StatusType.CARRY_ITEM);
            player.getRoomUser().setNeedsUpdate(true);
        }

        player.getRoomUser().getTimerManager().resetRoomTimer();
    }
}
