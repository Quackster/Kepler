package org.alexdev.kepler.game.catalogue;

import org.alexdev.kepler.dao.mysql.*;
import org.alexdev.kepler.game.catalogue.collectables.CollectableData;
import org.alexdev.kepler.game.catalogue.collectables.CollectablesManager;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.ItemManager;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.item.base.ItemDefinition;
import org.alexdev.kepler.game.item.interactors.InteractionType;
import org.alexdev.kepler.game.pets.PetManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.player.PlayerRank;
import org.alexdev.kepler.messages.outgoing.user.currencies.FILM;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.StringUtil;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CatalogueManager {
    private static CatalogueManager instance;

    private List<CataloguePage> cataloguePageList;
    private List<CatalogueItem> catalogueItemList;
    private List<CataloguePackage> cataloguePackageList;

    public CatalogueManager() {
        this.cataloguePageList = CatalogueDao.getPages();
        this.cataloguePackageList = CatalogueDao.getPackages();
        this.catalogueItemList = CatalogueDao.getItems();
        this.loadPackages();
    }

    /**
     * Purchase handler for player details.
     *
     * @param playerDetails the details for player
     * @param item the item the user is buying
     * @param extraData the extra data attached to item
     * @param overrideName the override name (used for trophies)
     * @param timestamp the time of purchase
     * @return the list of items bought
     * @throws SQLException the sql exception
     */
    public List<Item> purchase(PlayerDetails playerDetails, CatalogueItem item, String extraData, String overrideName, long timestamp) throws SQLException {
        List<Item> itemsBought = new ArrayList<>();

        if (!item.isPackage()) {
            //for (int i = 0; i < item.getAmount(); i++) {
                Item newItem = purchase(playerDetails, item.getDefinition(), extraData, item.getItemSpecialId(), overrideName, timestamp);

                if (newItem != null) {
                    itemsBought.add(newItem);
                }
            //}
        } else {
            for (CataloguePackage cataloguePackage : item.getPackages()) {
                for (int i = 0; i < cataloguePackage.getAmount(); i++) {
                    Item newItem = purchase(playerDetails, cataloguePackage.getDefinition(), null, cataloguePackage.getSpecialSpriteId(), overrideName, timestamp);

                    if (newItem != null) {
                        itemsBought.add(newItem);
                    }
                }
            }
        }

        return itemsBought;
    }

    /**
     * The player purchase handler but purchase single item.
     *
     * @param playerDetails the details of the player
     * @param def the definition of the item
     * @param extraData the extra data attached to the item
     * @param specialSpriteId the special sprite id used for posters
     * @param overrideName the override name - used for trophies
     * @param timestamp the time of purchase
     * @return the item bought
     * @throws SQLException the sql exception
     */
    public Item purchase(PlayerDetails playerDetails, ItemDefinition def, String extraData, int specialSpriteId, String overrideName, long timestamp) throws SQLException {
        Player player = PlayerManager.getInstance().getPlayerById(playerDetails.getId());

        // If the item is film, just give the user film
        if (def.getSprite().equals("film")) {
            CurrencyDao.increaseFilm(player.getDetails(), 5);
            player.send(new FILM(player.getDetails()));
            return null;
        }

        String customData = "";

        if (extraData != null) {
            if (def.hasBehaviour(ItemBehaviour.DECORATION)) {
                customData = extraData;
            } else {
                if (specialSpriteId > 0) {
                    customData = String.valueOf(specialSpriteId);
                }
            }

            if (def.hasBehaviour(ItemBehaviour.POST_IT)) {
                customData = "20";
            }

            if (def.hasBehaviour(ItemBehaviour.PRIZE_TROPHY)) {
                customData += (overrideName != null ? overrideName : player.getDetails().getName());
                customData += (char)9;

                customData += DateUtil.getShortDate(timestamp);
                customData += (char)9;

                customData += StringUtil.filterInput(extraData, true);
            }

            if (def.hasBehaviour(ItemBehaviour.ROOMDIMMER)) {
                customData = Item.DEFAULT_ROOMDIMMER_CUSTOM_DATA;
            }
        }

        Item item = new Item();
        item.setOwnerId(player.getDetails().getId());
        item.setDefinitionId(def.getId());

        if (def.getInteractionType() == InteractionType.PET_NEST) {
            if (extraData != null) {
                item.setDefinitionId(ItemManager.getInstance().getDefinitionBySprite("nest").getId());
            }
        }

        item.setCustomData(customData);

        ItemDao.newItem(item);
        player.getInventory().addItem(item);

        // If the item is a camera, give them 2 free film.
        if (def.getSprite().equals("camera")) {
            CurrencyDao.increaseFilm(player.getDetails(), 2);
            player.send(new FILM(player.getDetails()));
        }

        if (def.hasBehaviour(ItemBehaviour.TELEPORTER)) {
            Item linkedTeleporterItem = new Item();
            linkedTeleporterItem.setOwnerId(player.getDetails().getId());
            linkedTeleporterItem.setDefinitionId(def.getId());
            linkedTeleporterItem.setCustomData(customData);

            ItemDao.newItem(linkedTeleporterItem);
            player.getInventory().addItem(linkedTeleporterItem);

            linkedTeleporterItem.setTeleporterId(item.getId());
            item.setTeleporterId(linkedTeleporterItem.getId());

            TeleporterDao.addPair(linkedTeleporterItem.getId(), item.getId());
            TeleporterDao.addPair(item.getId(), linkedTeleporterItem.getId());
        }

        if (def.getInteractionType() == InteractionType.PET_NEST) {
            if (extraData != null) {
                String[] petData = extraData.split(Character.toString((char) 2));
                String name = StringUtil.filterInput(petData[0], true);
                String type = def.getSprite().replace("pets", "");
                int race = Integer.valueOf(petData[1]);
                String color = StringUtil.filterInput(petData[2], true);

                if (PetManager.getInstance().isValidName(player.getDetails().getName(), name)) {
                    PetDao.createPet(item.getId(), name, type, race, color);
                }
            }
        }

        return item;
    }

    /**
     * Load catalogue packages for all catalogue items.
     */
    private void loadPackages() {
        for (CatalogueItem catalogueItem : this.catalogueItemList) {
            if (!catalogueItem.isPackage()) {
                continue;
            }

            for (CataloguePackage cataloguePackage : this.cataloguePackageList) {
                if (catalogueItem.getSaleCode().equals(cataloguePackage.getSaleCode())) {
                    catalogueItem.getPackages().add(cataloguePackage);
                }
            }
        }
    }

    /**
     * Get a page by the page index
     *
     * @param pageIndex the index of the page to get for
     * @return the catalogue page
     */
    public CataloguePage getCataloguePage(String pageIndex) {
        for (CataloguePage cataloguePage : this.cataloguePageList) {
            if (cataloguePage.getNameIndex().equals(pageIndex)) {
                return cataloguePage;
            }
        }

        return null;
    }

    /**
     * Get an item by it's sale code.
     *
     * @param saleCode the item sale code identifier
     * @return the item, if successful
     */
    public CatalogueItem getCatalogueItem(String saleCode) {
        for (CatalogueItem catalogueItem : this.catalogueItemList) {
            if (catalogueItem.isHidden()) {
                continue;
            }

            if (catalogueItem.getSaleCode().equals(saleCode)) {
                return catalogueItem;
            }
        }

        return null;
    }

    /**
     * Get a list of items for the catalogue page.
     *
     * @param pageId the id of the page to get the items for
     * @return the list of items
     */
    public List<CatalogueItem> getCataloguePageItems(int pageId, boolean rawItems) {
        List<CatalogueItem> items = new ArrayList<>();

        // Ignore any game logic checks when requesting catalogue pages
        if (!rawItems) {
            /*
            if (pageId == GameConfiguration.getInstance().getInteger("rare.cycle.page.id")) {
                var itemList = getSeasonalItems();

                if (itemList.size() > 0) {
                    return itemList;
                }
            }
             */

            // Do collectables
            CollectableData collectableData = CollectablesManager.getInstance().getCollectableDataByPage(pageId);

            if (collectableData != null) {
                CatalogueItem catalogueItem = collectableData.getActiveItem();
                return List.of(catalogueItem);
            }
        }

        for (CatalogueItem catalogueItem : this.catalogueItemList) {
            if (catalogueItem.isHidden()) {
                continue;
            }

            if (catalogueItem.hasPage(pageId)) {
                items.add(catalogueItem);
            }
        }

        // items.sort(Comparator.comparingInt(CatalogueItem::getOrderId));
        return items;
    }

    /**
     * Get catalogue page list.
     *
     * @return the list of catalogue pages
     */
    public List<CataloguePage> getCataloguePages() {
        return this.cataloguePageList;
    }

    /**
     * Get catalogue page list for a certain rank
     *
     * @return the list of catalogue pages
     */
    public List<CataloguePage> getPagesForRank(PlayerRank minimumRank, boolean hasClub) {
        List<CataloguePage> cataloguePagesForRank = new ArrayList<>();

        for (CataloguePage page : this.cataloguePageList) {
            if (!page.isIndexVisible()) {
                continue;
            }

            if (page.isClubOnly() && !hasClub) {
                continue;
            }

            if (minimumRank.getRankId() >= page.getMinRole().getRankId()) {
                cataloguePagesForRank.add(page);
            }
        }

        return cataloguePagesForRank;
    }

    /**
     * Get catalogue items list.
     *
     * @return the list of items packages
     */
    public List<CatalogueItem> getCatalogueItems() {
        return catalogueItemList;
    }

    /**
     * Get the {@link CatalogueManager} instance
     *
     * @return the catalogue manager instance
     */
    public static CatalogueManager getInstance() {
        if (instance == null) {
            instance = new CatalogueManager();
        }

        return instance;
    }

    /**
     * Resets the catalogue manager singleton.
     */
    public static void reset() {
        instance = null;
        CatalogueManager.getInstance();
    }
}
