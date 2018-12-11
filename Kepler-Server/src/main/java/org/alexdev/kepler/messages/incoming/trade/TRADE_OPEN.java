package org.alexdev.kepler.messages.incoming.trade;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.room.managers.RoomTradeManager;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class TRADE_OPEN implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (!room.getCategory().hasAllowTrading()) {
            return;
        }

        if (player.getRoomUser().getTradePartner() != null) {
            return;
        }

        int instanceId = Integer.parseInt(reader.contents());
        Entity targetPartner = room.getEntityManager().getByInstanceId(instanceId);

        if (targetPartner == null) {
            return;
        }

        if (targetPartner.getType() != EntityType.PLAYER) {
            return;
        }

        Player tradePartner = (Player) targetPartner;

        RoomTradeManager.close(player.getRoomUser());
        RoomTradeManager.close(tradePartner.getRoomUser());

        player.getRoomUser().setStatus(StatusType.TRADE, "");
        player.getRoomUser().setNeedsUpdate(true);
        player.getRoomUser().setTradePartner(tradePartner);

        tradePartner.getRoomUser().setStatus(StatusType.TRADE, "");
        tradePartner.getRoomUser().setNeedsUpdate(true);
        tradePartner.getRoomUser().setTradePartner(player);

        RoomTradeManager.refreshWindow(player);
        RoomTradeManager.refreshWindow(tradePartner);
    }
}
