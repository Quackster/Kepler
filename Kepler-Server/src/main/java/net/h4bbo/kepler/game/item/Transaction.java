package net.h4bbo.kepler.game.item;

import net.h4bbo.kepler.util.DateUtil;
import org.apache.commons.lang3.StringUtils;

public class Transaction {
    private final String description;
    private final int costCoins;
    private final int costPixels;
    private final int amount;
    private final long createdAt;
    private final int itemId;

    public Transaction(String[] itemId, String description, int costCoins, int costPixels, int amount, long createdAt) {
        this.itemId = itemId.length > 0 ? (StringUtils.isNumeric(itemId[0]) ? Integer.parseInt(itemId[0]) : 0) : 0;
        this.description = description;
        this.costCoins = costCoins;
        this.costPixels = costPixels;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public int getItemId() {
        return itemId;
    }

    public String getFormattedDate() {
        return DateUtil.getDate(this.createdAt, "yyyy-MM-dd HH:mm a").replace("am", "AM").replace("pm","PM").replace(".", "");
    }

    public String getDescription() {
        return description;
    }

    public int getCostCoins() {
        return costCoins;
    }

    public int getCostPixels() {
        return costPixels;
    }

    public int getAmount() {
        return amount;
    }
}
