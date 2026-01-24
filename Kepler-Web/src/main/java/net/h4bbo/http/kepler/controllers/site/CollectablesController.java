package net.h4bbo.http.kepler.controllers.site;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.dao.mysql.CurrencyDao;
import net.h4bbo.kepler.dao.mysql.TransactionDao;
import net.h4bbo.kepler.game.catalogue.CatalogueManager;
import net.h4bbo.kepler.game.catalogue.collectables.CollectableData;
import net.h4bbo.kepler.game.catalogue.collectables.CollectablesManager;
import net.h4bbo.kepler.game.item.ItemManager;
import net.h4bbo.kepler.game.item.base.ItemDefinition;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.messages.incoming.catalogue.GRPC;
import net.h4bbo.kepler.server.rcon.messages.RconHeader;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.game.collectables.CollectableEntry;
import net.h4bbo.http.kepler.util.RconUtil;
import net.h4bbo.http.kepler.util.XSSUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CollectablesController {
    public static void collectables(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        int pageId = GameConfiguration.getInstance().getInteger("collectables.page");
        CollectableData collectablesData = CollectablesManager.getInstance().getCollectableDataByPage(pageId);

        webConnection.session().set("page", "credits");

        var template = webConnection.template("collectables");
        template.set("hasCollectable", (collectablesData != null));

        List<CollectableEntry> entries = new ArrayList<>();

        if (collectablesData != null) {
            collectablesData.checkCycle();

            template.set("collectableSprite", collectablesData.getActiveItem().getDefinition().getSprite());
            template.set("collectableName", collectablesData.getActiveItem().getDefinition().getName());
            template.set("collectableDescription", collectablesData.getActiveItem().getDefinition().getDescription());
            template.set("expireSeconds", collectablesData.getExpiry() - DateUtil.getCurrentTimeSeconds());


            for (String sprite : collectablesData.getSprites()) {
                ItemDefinition definition = ItemManager.getInstance().getDefinitionBySprite(sprite);
                entries.add(new CollectableEntry(sprite, definition.getName(), definition.getDescription()));
            }
        }

        template.set("collectablesShowroom", entries);
        template.render();
    }

    public static void confirm(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int pageId = GameConfiguration.getInstance().getInteger("collectables.page");
        CollectableData collectablesData = CollectablesManager.getInstance().getCollectableDataByPage(pageId);

        if (collectablesData == null) {
            webConnection.redirect("/");
            return;
        }

        var template = webConnection.template("habblet/collectiblesConfirm");

        template.set("collectableName", collectablesData.getActiveItem().getDefinition().getName());
        template.set("collectableCost", collectablesData.getActiveItem().getPrice());

        template.render();
    }

    public static void purchase(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        var template = webConnection.template("habblet/collectiblesPurchase");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        var pair = playerDetails.isBanned();

        if (pair != null) {
            webConnection.redirect("/account/banned");
            return;
        }

        int pageId = GameConfiguration.getInstance().getInteger("collectables.page");
        CollectableData collectablesData = CollectablesManager.getInstance().getCollectableDataByPage(pageId);

        if (collectablesData == null) {
            webConnection.redirect("/");
            return;
        }


        if (playerDetails.getCredits() >= collectablesData.getActiveItem().getPrice()/* &&
            playerDetails.getPixels() >= collectablesData.getActiveItem().getPricePixels()*/) {

            if (collectablesData.getActiveItem().getPrice() > 0) {
                CurrencyDao.decreaseCredits(playerDetails, collectablesData.getActiveItem().getPrice());
            }

            /*
            if (collectablesData.getActiveItem().getPricePixels() > 0) {
                CurrencyDao.decreasePixels(playerDetails, collectablesData.getActiveItem().getPricePixels());
            }*/

            template.set("message", "You've successfully bought a " + collectablesData.getActiveItem().getDefinition().getName());


            try {
                var items = CatalogueManager.getInstance().purchase(playerDetails, collectablesData.getActiveItem(), "", "", DateUtil.getCurrentTimeSeconds());

                String transactionDscription = GRPC.getTransactionDescription(collectablesData.getActiveItem());

                if (transactionDscription != null) {
                    TransactionDao.createTransaction(playerDetails.getId(),
                            items.stream().map(e -> String.valueOf(e.getId())).collect(Collectors.joining(",")),
                            collectablesData.getActiveItem().getId() + "",
                            1/*collectablesData.getActiveItem().getAmount()*/,
                            "Collectable " + transactionDscription,
                            collectablesData.getActiveItem().getPrice(), /*collectablesData.getActiveItem().getPricePixels(),*/0, true);
                }

            } catch (Exception ex) {
                template.set("message", "Purchasing the collectable failed due to system error");
            }

            RconUtil.sendCommand(RconHeader.REFRESH_HAND, new HashMap<>() {{
                put("userId", playerDetails.getId());
            }});

            RconUtil.sendCommand(RconHeader.REFRESH_CREDITS, new HashMap<>() {{
                put("userId", playerDetails.getId());
            }});
        } else {
            /*if (collectablesData.getActiveItem().getPricePixels() > playerDetails.getPixels()) {
                template.set("message", "Purchasing the collectable failed. You don't have enough pixels.");
            } else {
                template.set("message", "Purchasing the collectable failed. You don't have enough credits.");
            }*/

            if (collectablesData.getActiveItem().getPrice() > playerDetails.getCredits()) {
                template.set("message", "Purchasing the collectable failed. You don't have enough pixels.");
            }
        }

        template.render();
    }
}
