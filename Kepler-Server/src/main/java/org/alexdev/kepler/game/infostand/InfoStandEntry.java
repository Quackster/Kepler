package org.alexdev.kepler.game.infostand;

public class InfoStandEntry {

    private final String productId;
    private final InfoStandProp type;

    public InfoStandEntry(String productId, InfoStandProp type) {
        this.productId = productId;
        this.type = type;
    }

    public String getProductId() {
        return productId;
    }

    public InfoStandProp getType() {
        return type;
    }
}
