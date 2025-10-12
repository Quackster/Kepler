package org.alexdev.kepler.messages.incoming.infostand;

import org.alexdev.kepler.game.infostand.InfoStandManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.infostand.INFO_STAND_SHOP;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_INFOSTAND_SHOP_ITEMS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        player.send(new INFO_STAND_SHOP(player.getInfoStand(), InfoStandManager.getInstance().getShop()));
    }
}