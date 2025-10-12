package org.alexdev.kepler.game.infostand;

import org.alexdev.kepler.dao.mysql.InfoStandDao;

import java.util.List;

public class InfoStandManager {
    private static InfoStandManager instance;

    private final List<InfoStandShopEntry> shop;

    private InfoStandManager() {
        this.shop = InfoStandDao.getShop();
    }

    public List<InfoStandShopEntry> getShop() {
        return shop;
    }

    public InfoStandShopEntry getShopProduct(String productCode) {
        for (var entry : shop) {
            if (entry.getProductCode().equals(productCode)) {
                return entry;
            }
        }

        return null;
    }

    /**
     * Get the {@link InfoStandManager} instance
     *
     * @return the infostand shop manager instance
     */
    public static InfoStandManager getInstance() {
        if (instance == null) {
            instance = new InfoStandManager();
        }

        return instance;
    }
}
