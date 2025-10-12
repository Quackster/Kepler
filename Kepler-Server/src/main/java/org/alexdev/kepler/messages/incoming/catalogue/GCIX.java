package org.alexdev.kepler.messages.incoming.catalogue;

import org.alexdev.kepler.game.catalogue.CatalogueManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.catalogue.CATALOGUE_PAGES;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GCIX implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        final String editmode = reader.readString();
        final String language = reader.readString();

        player.send(new CATALOGUE_PAGES(
                CatalogueManager.getInstance().getPagesForRank(player.getDetails().getRank(), player.getDetails().hasClubSubscription())
        ));
    }
}
