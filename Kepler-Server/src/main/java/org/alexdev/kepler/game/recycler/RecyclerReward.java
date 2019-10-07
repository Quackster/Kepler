package org.alexdev.kepler.game.recycler;

import org.alexdev.kepler.game.catalogue.CatalogueItem;
import org.alexdev.kepler.game.catalogue.CatalogueManager;

public class RecyclerReward {
    private int id;
    private String saleCode;
    private int itemCost;

    public RecyclerReward(int id, String saleCode, int itemCost) {
        this.id = id;
        this.saleCode = saleCode;
        this.itemCost = itemCost;
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
}
