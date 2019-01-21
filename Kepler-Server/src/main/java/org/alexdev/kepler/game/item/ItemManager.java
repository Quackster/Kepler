package org.alexdev.kepler.game.item;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.game.item.base.ItemDefinition;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.util.DateUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class ItemManager {
    private static ItemManager instance;

    private Map<Integer, ItemDefinition> itemDefinitionMap;

    public ItemManager() {
        this.itemDefinitionMap = ItemDao.getItemDefinitions();
    }

    /**
     * Quick and easy method for creating gifts.
     *
     * @param playerDetails the user to give the gift to
     * @param saleCode the sprite to give
     * @return the item as gift
     */
    public Item createGift(PlayerDetails playerDetails, String saleCode, String presentLabel) {
        Item item = new Item();
        item.setDefinitionId(ItemManager.getInstance().getDefinitionBySprite("present_gen" + ThreadLocalRandom.current().nextInt(1, 7)).getId());
        item.setOwnerId(playerDetails.getId());
        item.setCustomData(saleCode +
                Item.PRESENT_DELIMETER + playerDetails.getName() +
                Item.PRESENT_DELIMETER + presentLabel + //From Habbo" +
                Item.PRESENT_DELIMETER +
                Item.PRESENT_DELIMETER + DateUtil.getCurrentTimeSeconds());

        try {
            ItemDao.newItem(item);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return item;
    }

    /**
     * Handle bulk item saving.
     *
     * @param itemSavingQueue the queue that's used for saving items
     */
    public void performItemSaving(BlockingQueue<Item> itemSavingQueue) {
        if (itemSavingQueue.isEmpty()) {
            return;
        }

        List<Item> itemList = new ArrayList<>();
        itemSavingQueue.drainTo(itemList);

        ItemDao.updateItems(itemList);
    }

    /**
     * Get a item definition by the definition id.
     *
     * @param definitionId the definition id to get for
     * @return the item definition
     */
    public ItemDefinition getDefinition(int definitionId) {
        if (this.itemDefinitionMap.containsKey(definitionId)) {
            return this.itemDefinitionMap.get(definitionId);
        }

        return null;
    }

    /**
     * Get a item definition by sprite name.
     *
     * @param spriteName the name of the sprite to locate the definition
     * @return the item definition
     */
    public ItemDefinition getDefinitionBySprite(String spriteName) {
        for (ItemDefinition definition : this.itemDefinitionMap.values()) {
            if (definition.getSprite().equals(spriteName)) {
                return definition;
            }
        }

        return null;
    }

    /**
     * Get the {@link ItemManager} instance
     *
     * @return the item manager instance
     */
    public static ItemManager getInstance() {
        if (instance == null) {
            instance = new ItemManager();
        }

        return instance;
    }

    /**
     * Resets the item manager singleton.
     */
    public static void reset() {
        instance = null;
        ItemManager.getInstance();
    }
}
