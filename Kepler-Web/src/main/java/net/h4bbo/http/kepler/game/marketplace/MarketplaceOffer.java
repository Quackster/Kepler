package net.h4bbo.http.kepler.game.marketplace;

import net.h4bbo.kepler.game.item.ItemManager;
import net.h4bbo.kepler.game.item.base.ItemDefinition;
import net.h4bbo.kepler.util.DateUtil;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MarketplaceOffer {
    private final long id;
    private final int userId;
    private final long itemId;
    private final int definitionId;
    private final int price;
    private final int lowestPrice;
    private final int rotation;
    private final int state;
    private final int colour;
    private final long createdAt;
    private final boolean isActive;
    private final int itemsAlike;

    public MarketplaceOffer(long id, int userId, long itemId, int definitionId, int price, int lowestPrice, int rotation, int state, int colour, int itemsAlike, long createdAt, boolean isActive) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
        this.definitionId = definitionId;
        this.price = price;
        this.lowestPrice = lowestPrice;
        this.rotation = rotation;
        this.state = state;
        this.colour = colour;
        this.createdAt = createdAt;
        this.isActive = isActive;
        this.itemsAlike = itemsAlike;
    }

    public String getTimeUntilExpiry() {
        long timestamp = this.createdAt + TimeUnit.DAYS.toSeconds(7);
        return DateUtil.getMarketplaceReadableSeconds(timestamp - DateUtil.getCurrentTimeSeconds());
    }

    public long getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public ItemDefinition getDefinition() {
        return ItemManager.getInstance().getDefinition(this.definitionId);
    }

    public long getItemId() {
        return itemId;
    }

    public int getDefinitionId() {
        return definitionId;
    }

    public int getPrice() {
        return price;
    }

    public String getFormattedPrice() {
        return NumberFormat.getInstance(Locale.US).format(price);
    }

    public int getLowestPrice() {
        return lowestPrice;
    }

    public String getFormattedLowestPrice() {
        return NumberFormat.getInstance(Locale.US).format(lowestPrice);
    }


    public int getRotation() {
        return rotation;
    }

    public int getState() {
        return state;
    }

    public int getColour() {
        return colour;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getItemsAlike() {
        return itemsAlike;
    }
}
