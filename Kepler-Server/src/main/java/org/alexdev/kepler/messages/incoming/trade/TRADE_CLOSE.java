package org.alexdev.kepler.messages.incoming.trade;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.managers.RoomTradeManager;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class TRADE_CLOSE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (player.getRoomUser().getTradePartner() == null) {
            return;
        }

        RoomTradeManager.close(player.getRoomUser());
    }
}
