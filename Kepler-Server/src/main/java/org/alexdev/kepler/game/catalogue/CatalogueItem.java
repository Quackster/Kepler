package org.alexdev.kepler.game.catalogue;

import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.item.base.ItemDefinition;
import org.alexdev.kepler.game.item.ItemManager;
import org.alexdev.kepler.game.texts.TextsManager;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class CatalogueItem {
    private String saleCode;
    private int orderId;
    private boolean isHidden;
    private int price;
    private ItemDefinition definition;
    private int itemSpecialId;
    private List<CataloguePackage> packages;
    private int[] pages;

    private String packageName;
    private String packageDescription;
    private boolean isPackage;

    public CatalogueItem(String saleCode, String pageId, int orderId, int price, boolean hidden, int definitionId, int itemSpecialId, boolean isPackage, String packageName, String packageDescription) {
        int[] pages = new int[pageId.split(",").length];

        int i = 0;
        if (pageId.length() > 0) {
            for (String data : pageId.split(",")) {
                pages[i++] = Integer.parseInt(data);
            }
        }

        this.setPageData(saleCode, pages, orderId, price, hidden, definitionId, itemSpecialId, isPackage, packageName, packageDescription);
    }


    public CatalogueItem(String saleCode, int[] pages, int orderId, int price, boolean hidden, int definitionId, int itemSpecialId, boolean isPackage, String packageName, String packageDescription) {
        this.setPageData(saleCode, pages, orderId, price, hidden, definitionId, itemSpecialId, isPackage, packageName, packageDescription);
    }

    private void setPageData(String saleCode, int[] pages, int orderId, int price, boolean hidden, int definitionId, int itemSpecialId, boolean isPackage, String packageName, String packageDescription) {
        this.saleCode = saleCode;
        this.orderId = orderId;
        this.price = price;
        this.isHidden = hidden;
        this.definition = ItemManager.getInstance().getDefinition(definitionId);
        this.itemSpecialId = itemSpecialId;
        this.isPackage = isPackage;
        this.packages = new ArrayList<>();
        this.pages = pages;
        this.packageName = packageName;
        this.packageDescription = packageDescription;

        if (this.definition == null && !this.isPackage) {
            System.out.println("Item (" + this.saleCode + ") has an invalid definition id: " + definitionId);
        }
    }


    public String getName() {
        if (this.isPackage) {
            return this.packageName;
        }

        String name = TextsManager.getInstance().getValue(this.definition.getName(this.itemSpecialId));

        if (name.isEmpty()) {
            if (this.definition.getSprite().equals("film")) {
                return StringUtils.capitalize(this.definition.getSprite());
            }

            return this.definition.getName(this.itemSpecialId);
        }

        return name;
    }

    public String getDescription() {
        if (this.isPackage) {
            return this.packageDescription;
        }

        String description = TextsManager.getInstance().getValue(this.definition.getDescription(this.itemSpecialId));

        if (description.isEmpty()) {
            return this.definition.getDescription(this.itemSpecialId);
        }

        return description;
    }

    public String getType() {
        if (this.isPackage) {
            return "d";
        } else {
            if (this.definition.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
                return "i";
            } else {
                return "s";
            }
        }
    }

    public String getIcon() {
        if (this.isPackage) {
            return "";
        }

        return this.definition.getIcon(this.itemSpecialId);
    }

    public String getSize() {
        if (this.isPackage || this.definition.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
            return "";
        } else {
            return "0";
        }
    }

    public String getDimensions() {
        if (this.isPackage || this.definition.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
            return "";
        } else {
            return this.definition.getLength() + "," + this.definition.getWidth();
        }
    }

    public String getSaleCode() {
        return saleCode;
    }

    public int[] getPageS() {
        return pages;
    }

    public ItemDefinition getDefinition() {
        return definition;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getItemSpecialId() {
        return itemSpecialId;
    }

    public boolean isPackage() {
        return isPackage;
    }

    public List<CataloguePackage> getPackages() {
        return packages;
    }


    /**
     * Copy the catalogue item instance so we can set prices that won't affect the main instance.
     *
     * @return the new catalogue item instance
     */
    public CatalogueItem copy() {
        return new CatalogueItem(this.saleCode, this.pages, this.orderId, this.price, this.isHidden, this.definition.getId(), this.itemSpecialId, this.isPackage, this.packageName, this.packageDescription);
    }

    public boolean isHidden() {
        return isHidden;
    }

    public boolean hasPage(int pageId) {
        for (int page : this.pages) {
            if (page == pageId) {
                return true;
            }
        }

        return false;
    }
}
