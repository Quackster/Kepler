package org.alexdev.kepler.game.purse;

import org.alexdev.kepler.game.item.base.ItemDefinition;

import java.util.List;

public class Voucher {
    public final int credits;
    public final List<ItemDefinition> items;
    
    public Voucher(List<ItemDefinition> voucherItems, int voucherCredits) {
        credits = voucherCredits;
        items = voucherItems;
    }
    
    public int getCredits() {
        return credits;
    }
    
    public List<ItemDefinition> getItemDefinitions() {
        return items;
    }
}
