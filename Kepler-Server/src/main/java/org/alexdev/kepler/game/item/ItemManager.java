package org.alexdev.kepler.game.item;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.game.item.base.ItemDefinition;
import org.alexdev.kepler.game.item.public_items.PublicItemData;

import java.util.List;
import java.util.Map;

public class ItemManager {
    private static ItemManager instance;

    private Map<Integer, ItemDefinition> itemDefinitionMap;

    public ItemManager() {
        this.itemDefinitionMap = ItemDao.getItemDefinitions();
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
