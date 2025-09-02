package net.h4bbo.kepler.messages.incoming.rooms.teleporter;

import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.rooms.items.BROADCAST_TELEPORTER;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class DOORGOIN implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        int itemId = Integer.parseInt(reader.contents());

        if (player.getRoomUser().getAuthenticateTelporterId() == itemId) {
            Item item = player.getRoomUser().getRoom().getItemManager().getById(itemId);
            player.getRoomUser().getRoom().send(new BROADCAST_TELEPORTER(item, player.getDetails().getName(), false));
        }
    }
}
