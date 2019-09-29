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

    public int getId() {
        return id;
    }

    public String getSaleCode() {
        return saleCode;
    }

    public CatalogueItem getCatalogueItem() {
        return CatalogueManager.getInstance().getCatalogueItem(this.saleCode);
    }

    public int getItemCost() {
        return itemCost;
    }
}
