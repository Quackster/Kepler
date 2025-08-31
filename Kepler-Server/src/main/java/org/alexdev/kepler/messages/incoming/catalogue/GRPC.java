package org.alexdev.kepler.messages.incoming.catalogue;

import org.alexdev.kepler.dao.mysql.CurrencyDao;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.dao.mysql.TransactionDao;
import org.alexdev.kepler.game.catalogue.*;
import org.alexdev.kepler.game.catalogue.collectables.CollectablesManager;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.ItemManager;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.item.base.ItemDefinition;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.texts.TextsManager;
import org.alexdev.kepler.messages.outgoing.alert.ALERT;
import org.alexdev.kepler.messages.outgoing.alert.NO_USER_FOUND;
import org.alexdev.kepler.messages.outgoing.catalogue.NO_CREDITS;
import org.alexdev.kepler.messages.outgoing.rooms.items.ITEM_DELIVERED;
import org.alexdev.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.StringUtil;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GRPC implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        String content = reader.contents();
        String[] data = content.split(Character.toString((char) 13));

        String saleCode = data[3];

        CatalogueItem item = CatalogueManager.getInstance().getCatalogueItem(saleCode);

        if (item == null) {
            return;
        }

        int price = item.getPrice();

        // If the item is not a buyable special rare, then check if they can actually buy it
        if (RareManager.getInstance().getCurrentRare() != null && item != RareManager.getInstance().getCurrentRare()) {
            Optional<CataloguePage> pageStream = CatalogueManager.getInstance().getCataloguePages().stream().filter(p -> item.hasPage(p.getId())).findFirst();

            if (!pageStream.isPresent() || pageStream.get().getMinRole().getRankId() > player.getDetails().getRank().getRankId()) {
                return;
            }
        }

        var currentRare = RareManager.getInstance().getCurrentRare();

        if (currentRare != null && currentRare == item) {
            if (!player.hasFuse(Fuseright.CREDITS)) {
                price = RareManager.getInstance().getRareCost().get(currentRare);
            }
        }

        if (price > player.getDetails().getCredits()) {
            player.send(new NO_CREDITS());
            return;
        }

        if (data[5].equals("1")) { // It's a gift!
            PlayerDetails receivingUserDetails = PlayerManager.getInstance().getPlayerData(PlayerDao.getId(data[6]));

            //if (!data[6].toLowerCase().equals(player.getDetails().getName().toLowerCase())) {
            if (receivingUserDetails == null) {
                player.send(new NO_USER_FOUND(data[6]));
                return;
            }
            //}

            String presentNote = "";
            String extraData = data[4];

            try {
                presentNote = data[7];
            } catch (Exception ignored) {
                presentNote = "";
            }

            if (presentNote.isEmpty()) {
                presentNote = " ";
            }
            
            extraData = extraData.replace(Item.PRESENT_DELIMETER, "");
            presentNote = presentNote.replace(Item.PRESENT_DELIMETER, "");

            if (item.getDefinition() != null && item.getDefinition().getSprite().equals("poster")) {
                extraData = String.valueOf(item.getItemSpecialId());
            }

            Item present = ItemManager.getInstance().createGift(receivingUserDetails.getId(), player.getDetails().getName(), item.getSaleCode(), StringUtil.filterInput(presentNote, false), extraData);//new Item();
            /*present.setOwnerId(receivingUserDetails.getId());
            present.setDefinitionId(ItemManager.getInstance().getDefinitionBySprite("present_gen" + ThreadLocalRandom.current().nextInt(1, 7)).getId());
            present.setCustomData(saleCode +
                    Item.PRESENT_DELIMETER + player.getDetails().getName() +
                    Item.PRESENT_DELIMETER + StringUtil.filterInput(presentNote, true) +
                    Item.PRESENT_DELIMETER + extraData +
                    Item.PRESENT_DELIMETER + DateUtil.getCurrentTimeSeconds());

            ItemDao.newItem(present);*/

            String transactionDscription = getTransactionDescription(item);

            if (transactionDscription != null && receivingUserDetails != null) {
                TransactionDao.createTransaction(receivingUserDetails.getId(),
                        present.getId() + "",
                        item.getId() + "",
                        1/*item.getAmount()*/,
                        "Gift purchase from " + player.getDetails().getName() + " for " + receivingUserDetails.getName() + " - " + transactionDscription, item.getPrice(), 0/*pricePixels*/, true);

                TransactionDao.createTransaction(player.getDetails().getId(),
                        present.getId() + "",
                        item.getId() + "",
                        1/*item.getAmount()*/,
                        "Gift purchase from " + player.getDetails().getName() + " for " + receivingUserDetails.getName() + " - " + transactionDscription, item.getPrice(), 0/*pricePixels*/, true);
            }

            Player receiver = PlayerManager.getInstance().getPlayerById(receivingUserDetails.getId());

            if (receiver != null) {
                receiver.getInventory().addItem(present);
                receiver.getInventory().getView("last");
                //receiver.send(new ITEM_DELIVERED());
            }

            player.send(new ALERT(TextsManager.getInstance().getValue("successfully_purchase_gift_for").replace("%user%", receivingUserDetails.getName())));
            //player.send(new DELIVER_PRESENT(present));
        } else {
            String extraData = null;

            if (!item.isPackage()) {
                extraData = data[4];
            }

            var items = CatalogueManager.getInstance().purchase(player.getDetails(), item, extraData, null, DateUtil.getCurrentTimeSeconds());

            if (items.size() > 0)
                player.getInventory().getView("new");


        String transactionDscription = getTransactionDescription(item);

        if (transactionDscription != null) {
            boolean isCollectable = CollectablesManager.getInstance().isCollectable(item);

            if (isCollectable) {
                TransactionDao.createTransaction(player.getDetails().getId(),
                        items.stream().map(e -> String.valueOf(e.getId())).collect(Collectors.joining(",")),
                        item.getId() + "",
                        1/*item.getAmount()*/,
                        "Collectible - " + transactionDscription, item.getPrice(), 0/*pricePixels*/, true);
            } else {
                TransactionDao.createTransaction(player.getDetails().getId(),
                        items.stream().map(e -> String.valueOf(e.getId())).collect(Collectors.joining(",")),
                        item.getId() + "",
                        1/*item.getAmount()*/, transactionDscription, item.getPrice(), 0/*pricePixels*/, true);
            }
        }

            boolean showItemDelivered = true;

            // Don't send item delivered if they just buy film
            if (item.getDefinition() != null && item.getDefinition().getSprite().equals("film")) {
                showItemDelivered = false;
            }

            if (showItemDelivered) {
                if (!GameConfiguration.getInstance().getBoolean("disable.purchase.successful.alert")) {
                    player.send(new ITEM_DELIVERED());
                }
            }
        }


        CurrencyDao.decreaseCredits(player.getDetails(), price);
        player.send(new CREDIT_BALANCE(player.getDetails().getCredits()));
    }

    public static String getTransactionDescription(CatalogueItem item) {
        if (!item.isPackage()) {
            return getItemDescription(item.getDefinition(), 1/*item.getAmount()*/);
        } else {
            List<String> descriptions = new ArrayList<>();

            for (CataloguePackage cataloguePackage : item.getPackages()) {
                var description = getItemDescription(cataloguePackage.getDefinition(), 1);

                if (description != null) {
                    descriptions.add(description);
                }
            }

            return "Package purchase (" + String.join(", ", descriptions) + ")";
        }
    }

    private static String getItemDescription(ItemDefinition definition, int amount) {
        if (definition == null) {
            return null;
        }

        /*
        if (definition.hasBehaviour(ItemBehaviour.EFFECT)) {
            return "Effect " + definition.getSprite().replace("avatar_effect", "") + " purchase";
        }*/

        if (definition.getSprite().equals("film")) {
            return "Film purchase";
        }

        return definition.getName() + " purchase";
    }
}
