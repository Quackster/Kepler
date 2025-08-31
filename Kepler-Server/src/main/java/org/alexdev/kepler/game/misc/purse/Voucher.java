package org.alexdev.kepler.game.misc.purse;

import java.util.ArrayList;
import java.util.List;

public class Voucher {
    public final int credits;
    public final List<String> items;
    private final boolean allowNewUsers;

    public Voucher(int voucherCredits, boolean allowNewUsers) {
        credits = voucherCredits;
        items = new ArrayList<>();
        this.allowNewUsers = allowNewUsers;
    }

    /**
     * Get the amount of redeemable credits.
     *
     * @return the redeemable credit amount
     */
    public int getCredits() {
        return credits;
    }

    /**
     * Get the item amount.
     *
     * @return the item amount
     */
    public List<String> getItems() {
        return items;
    }

    /**
     * Should users less than a day old be able to redeem this?
     *
     * @return true, if successful
     */
    public boolean isAllowNewUsers() {
        return allowNewUsers;
    }
}

