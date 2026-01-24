package net.h4bbo.http.kepler.game.housekeeping;

public class MarketplacePurchaseOccurance {
    private final int userId;
    private final String username;
    private final int purchaseCount;

    public MarketplacePurchaseOccurance(int userId, String username, int purchaseCount) {
        this.userId = userId;
        this.username = username;
        this.purchaseCount = purchaseCount;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getPurchaseCount() {
        return purchaseCount;
    }
}
