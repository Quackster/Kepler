package org.alexdev.kepler.messages.incoming.catalogue;

import org.alexdev.kepler.game.catalogue.CatalogueItem;
import org.alexdev.kepler.game.catalogue.CatalogueManager;
import org.alexdev.kepler.game.catalogue.CataloguePage;
import org.alexdev.kepler.game.catalogue.RareManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.catalogue.CATALOGUE_PAGE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GCAP implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        String pageName = reader.contents().split("/")[1];

        CataloguePage cataloguePage = CatalogueManager.getInstance().getCataloguePage(pageName);

        if (cataloguePage == null) {
            return;
        }

        if (player.getDetails().getRank().getRankId() >= cataloguePage.getFuses().getMinRole().getRankId()) {
            List<CatalogueItem> catalogueItemList = CatalogueManager.getInstance().getCataloguePageItems(cataloguePage.getId());

            player.send(new CATALOGUE_PAGE(
                    cataloguePage,
                    catalogueItemList));
        }
    }
}
