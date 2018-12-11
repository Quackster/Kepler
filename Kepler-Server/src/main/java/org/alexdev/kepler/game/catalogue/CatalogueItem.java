package org.alexdev.kepler.game.catalogue;

import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.item.base.ItemDefinition;
import org.alexdev.kepler.game.item.ItemManager;
import org.alexdev.kepler.game.texts.TextsManager;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CatalogueItem {
    private String saleCode;
    private int pageId;
    private int orderId;
    private boolean isHidden;
    private int price;
    private ItemDefinition definition;
    private int itemSpecialId;
    private String packageName;
    private String packageDescription;
    private boolean isPackage;
    private List<CataloguePackage> packages;

    public CatalogueItem(String saleCode, int pageId, int orderId, boolean isHidden, int price, int definitionId, int itemSpecialId, String packageName, String packageDescription, boolean isPackage) {
        this.saleCode = saleCode;
        this.pageId = pageId;
        this.orderId = orderId;
        this.isHidden = isHidden;
        this.price = price;
        this.definition = ItemManager.getInstance().getDefinition(definitionId);
        this.itemSpecialId = itemSpecialId;
        this.packageName = packageName;
        this.packageDescription = packageDescription;
        this.isPackage = isPackage;
        this.packages = new ArrayList<>();

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

    public int getPageId() {
        return pageId;
    }

    public int getOrderId() {
        return orderId;
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

    public String getPackageName() {
        return packageName;
    }

    public String getPackageDescription() {
        return packageDescription;
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
        return new CatalogueItem(this.saleCode, this.pageId, this.orderId, this.isHidden, this.price, this.definition.getId(), this.itemSpecialId, this.packageName, this.packageDescription, this.isPackage);
    }

    public boolean isHidden() {
        return isHidden;
    }
}
