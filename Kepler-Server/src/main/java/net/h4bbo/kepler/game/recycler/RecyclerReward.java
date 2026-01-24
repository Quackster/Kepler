package net.h4bbo.kepler.game.recycler;

import net.h4bbo.kepler.game.catalogue.CatalogueItem;
import net.h4bbo.kepler.game.catalogue.CatalogueManager;

public class RecyclerReward {
    private int id;
    private String saleCode;
    private int itemCost;
    private final int recyclingTimeSessions;
    private final int collectionTimeSeconds;

    public RecyclerReward(int id, String saleCode, int itemCost, int recyclingTimeSessions, int collectionTimeSeconds) {
        this.id = id;
        this.saleCode = saleCode;
        this.itemCost = itemCost;
        this.recyclingTimeSessions = recyclingTimeSessions;
        this.collectionTimeSeconds = collectionTimeSeconds;
    }

    /**
     * Get the id of the recycler reward
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Get the sale code (the same one for catalogue items).
     *
     * @return the sale code
     */
    public String getSaleCode() {
        return saleCode;
    }

    /**
     * Get the catalogue item this is linked to.
     *
     * @return the catalogue item
     */
    public CatalogueItem getCatalogueItem() {
        return CatalogueManager.getInstance().getCatalogueItem(this.saleCode);
    }

    /**
     * Get the amount of recyclable items required.
     *
     * @return the amount of items required
     */
    public int getItemCost() {
        return itemCost;
    }

    /**
     * Get the time in seconds it takes for this to recycle.
     *
     * @return the time in seconds it takes to recycle
     */
    public int getRecyclingTimeSessions() {
        return recyclingTimeSessions;
    }

    /**
     * Get the collection time in seconds before you can longer get the furniture
     *
     * @return the time in seconds
     */
    public int getCollectionTimeSeconds() {
        return collectionTimeSeconds;
    }
}
