package net.h4bbo.kepler.messages.incoming.catalogue;

import net.h4bbo.kepler.game.catalogue.CatalogueManager;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.catalogue.CATALOGUE_PAGES;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class GCIX implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        player.send(new CATALOGUE_PAGES(
                CatalogueManager.getInstance().getPagesForRank(player.getDetails().getRank(), player.getDetails().hasClubSubscription())
        ));
    }
}
