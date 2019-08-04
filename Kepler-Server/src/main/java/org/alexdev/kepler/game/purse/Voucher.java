package org.alexdev.kepler.game.purse;

import java.util.ArrayList;
import java.util.List;

public class Voucher {
    public final int credits;
    public final List<String> items;
    
    public Voucher(int voucherCredits) {
        credits = voucherCredits;
        items = new ArrayList<>();
    }
    
    public int getCredits() {
        return credits;
    }
    
    public List<String> getItems() {
        return items;
    }
}
