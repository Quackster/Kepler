package org.alexdev.kepler.game.purse;

import org.alexdev.kepler.game.item.base.ItemDefinition;

import java.util.ArrayList;
import java.util.List;

public class Voucher {
    public final int credits;
    public final List<ItemDefinition> items;
    
    public Voucher(int voucherCredits) {
        credits = voucherCredits;
        items = new ArrayList<>();
    }
    
    public int getCredits() {
        return credits;
    }
    
    public List<ItemDefinition> getItemDefinitions() {
        return items;
    }
}
