package net.h4bbo.kepler.messages.incoming.trade;

import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.managers.RoomTradeManager;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class TRADE_ADDITEM implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (player.getRoomUser().getTradePartner() == null) {
            return;
        }

        int itemId = Integer.parseInt(reader.contents());
        Item inventoryItem = player.getInventory().getItem(itemId);

        if (inventoryItem == null) {
            return;
        }

        player.getRoomUser().getTradeItems().add(inventoryItem);

        RoomTradeManager.refreshWindow(player);
        RoomTradeManager.refreshWindow(player.getRoomUser().getTradePartner());
    }
}
