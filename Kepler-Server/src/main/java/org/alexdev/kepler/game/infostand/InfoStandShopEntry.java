package org.alexdev.kepler.game.infostand;

public class InfoStandShopEntry {
    private final int id;
    private final String productId;
    private final String productCode;
    private final InfoStandProp type;
    private final int currency;
    private final int price;

    public InfoStandShopEntry(int id, String productId, String productCode, InfoStandProp type, int currency, int price) {
        this.id = id;
        this.productId = productId;
        this.productCode = productCode;
        this.type = type;
        this.currency = currency;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public InfoStandProp getType() {
        return type;
    }

    public int getCurrency() {
        return currency;
    }

    public int getPrice() {
        return price;
    }
}
