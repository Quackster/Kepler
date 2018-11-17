package org.alexdev.kepler.messages.incoming.catalogue;

import org.alexdev.kepler.game.catalogue.CatalogueItem;
import org.alexdev.kepler.game.catalogue.CatalogueManager;
import org.alexdev.kepler.game.catalogue.CataloguePage;
import org.alexdev.kepler.game.catalogue.RareManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.catalogue.CATALOGUE_PAGE;
import org.alexdev.kepler.messages.outgoing.catalogue.CATALOGUE_PAGES;
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

        if (player.getDetails().getRank().getRankId() >= cataloguePage.getMinRole().getRankId()) {
            List<CatalogueItem> catalogueItemList = CatalogueManager.getInstance().getCataloguePageItems(cataloguePage.getId());

            if (RareManager.getInstance().getCurrentRare() != null &&
                    cataloguePage.getId() == GameConfiguration.getInstance().getInteger("rare.cycle.page.id")) {

                var currentRare = RareManager.getInstance().getCurrentRare();

                var rareItem = currentRare.copy();
                rareItem.setPrice(RareManager.getInstance().getRareCost().get(currentRare));
                catalogueItemList = List.of(rareItem);

                TimeUnit rareManagerUnit = TimeUnit.valueOf(GameConfiguration.getInstance().getString("rare.cycle.refresh.timeunit"));

                long interval = rareManagerUnit.toSeconds(GameConfiguration.getInstance().getInteger("rare.cycle.refresh.interval"));
                long currentTick = RareManager.getInstance().getTick().get();
                long timeUntil = interval - currentTick;

                cataloguePage.setBody("Okay this thing is fucking epic!<br><br>The time until the next rare is " + DateUtil.getReadableSeconds(timeUntil) + "!");
            }

            player.send(new CATALOGUE_PAGE(
                    cataloguePage,
                    catalogueItemList));
        }
    }
}
