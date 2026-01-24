package net.h4bbo.kepler.game.catalogue.collectables;

import net.h4bbo.kepler.dao.mysql.CollectablesDao;
import net.h4bbo.kepler.game.catalogue.CatalogueItem;
import net.h4bbo.kepler.game.catalogue.CatalogueManager;
import net.h4bbo.kepler.util.DateUtil;

public class CollectableData {
    private int currentPosition;
    private long expiry;
    private final long lifetime;

    private int collectablesStorePage;
    private int collectablesAdminPage;

    private final String[] classNames;

    public CollectableData(int collectablesStorePage, int collectablesAdminPage, long expiry, long lifetime, int currentPosition, String[] classNames) {
        this.collectablesStorePage = collectablesStorePage;
        this.collectablesAdminPage = collectablesAdminPage;
        this.expiry = expiry;
        this.lifetime = lifetime;
        this.currentPosition = currentPosition;
        this.classNames = classNames;
    }

    /**
     * The method for checking if the next collectable is due.
     */
    public void checkCycle() {
        if (!(DateUtil.getCurrentTimeSeconds() > this.expiry)) {
            return;
        }

        this.currentPosition++;

        if (this.currentPosition >= this.classNames.length) {
            this.currentPosition = 0;
        }

        this.expiry = DateUtil.getCurrentTimeSeconds() + this.lifetime;
        CollectablesDao.saveData(this.collectablesStorePage, this.currentPosition, this.expiry);
    }

    /**
     * Gets the catalogue item of the active collectable.
     *
     * @return the catalogue item
     */
    public CatalogueItem getActiveItem() {
        String className = this.classNames[this.currentPosition];

        for (CatalogueItem item : CatalogueManager.getInstance().getCataloguePageItems(this.collectablesAdminPage, true)) {
            if (item.getDefinition().getSprite().equals(className)) {
                var collectable = item.copy();
                return collectable;
            }
        }

        return null;
    }

    public String[] getSprites() {
        return this.classNames;
    }

    public long getExpiry() {
        return expiry;
    }

    public int getCollectablesStorePage() {
        return collectablesStorePage;
    }

    public int getCollectablesAdminPage() {
        return collectablesAdminPage;
    }
}
