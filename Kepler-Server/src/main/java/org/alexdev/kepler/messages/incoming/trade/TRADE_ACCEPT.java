package org.alexdev.kepler.messages.incoming.trade;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.managers.RoomTradeManager;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class TRADE_ACCEPT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (player.getRoomUser().getTradePartner() == null) {
            return;
        }

        player.getRoomUser().setTradeAccept(true);

        RoomTradeManager.refreshWindow(player);
        RoomTradeManager.refreshWindow(player.getRoomUser().getTradePartner());

        if (player.getRoomUser().hasAcceptedTrade() &&
            player.getRoomUser().getTradePartner().getRoomUser().hasAcceptedTrade()) {

            RoomTradeManager.addItems(player, player.getRoomUser().getTradePartner());
            RoomTradeManager.addItems(player.getRoomUser().getTradePartner(), player);

            RoomTradeManager.close(player.getRoomUser());
        }
    }
}
