package org.alexdev.kepler.game.catalogue.collectables;

import org.alexdev.kepler.dao.mysql.CollectablesDao;
import org.alexdev.kepler.game.catalogue.CatalogueItem;

import java.util.List;

public class CollectablesManager {
    private static CollectablesManager instance;
    private List<CollectableData> collectableDataList;

    public CollectablesManager() {
        this.collectableDataList = CollectablesDao.getCollectablesData();
    }

    /**
     * Checks all the collectable pages for expired items and cycles to the next item if required.
     */
    public void checkExpiries() {
        for (CollectableData collectableData : this.collectableDataList) {
            collectableData.checkCycle();
        }
    }

    /**
     * Gets if the item is currently on sale as a collectable.
     *
     * @param item the item to check
     * @return true, if successful
     */
    public boolean isCollectable(CatalogueItem item) {
        for (CollectableData collectableData : this.collectableDataList) {
            CatalogueItem collectableItem = collectableData.getActiveItem();

            if (collectableItem != null && collectableItem.getId() == item.getId()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get collectable data by the page id.
     *
     * @param pageId the page id to get data for
     * @return the collectable data instance
     */
    public CollectableData getCollectableDataByPage(int pageId) {
        for (CollectableData collectableData : this.collectableDataList) {
            if (collectableData.getCollectablesStorePage() == pageId || collectableData.getCollectablesAdminPage() == pageId) {
                return collectableData;
            }
        }

        return null;
    }

    /**
     * Get collectable data by the page id.
     *
     * @param itemId the page id to get data for
     * @return the collectable data instance
     */
    public CollectableData getCollectableDataByItem(int itemId) {
        for (CollectableData collectableData : this.collectableDataList) {
            if (collectableData.getActiveItem().getId() == itemId) {
                return collectableData;
            }
        }

        return null;
    }

    /**
     * Get the {@link CollectablesManager} instance
     *
     * @return the collectables manager instance
     */
    public static CollectablesManager getInstance() {
        if (instance == null) {
            instance = new CollectablesManager();
        }

        return instance;
    }


    /**
     * Resets the catalogue manager singleton.
     */
    public static void reset() {
        instance = null;
        CollectablesManager.getInstance();
    }
}
