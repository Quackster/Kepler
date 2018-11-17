package org.alexdev.kepler.game.catalogue;

import org.alexdev.kepler.dao.mysql.CatalogueDao;
import org.alexdev.kepler.game.player.PlayerRank;

import java.util.ArrayList;
import java.util.List;

public class CatalogueManager {
    private static CatalogueManager instance;

    private List<CataloguePage> cataloguePageList;
    private List<CatalogueItem> catalogueItemList;
    private List<CataloguePackage> cataloguePackageList;

    public CatalogueManager() {
        this.cataloguePageList = CatalogueDao.getPages();
        this.cataloguePackageList = CatalogueDao.getPackages();
        this.catalogueItemList = CatalogueDao.getItems();
        this.loadPackages();
    }

    /**
     * Load catalogue packages for all catalogue items.
     */
    private void loadPackages() {
        for (CatalogueItem catalogueItem : this.catalogueItemList) {
            if (!catalogueItem.isPackage()) {
                continue;
            }

            for (CataloguePackage cataloguePackage : this.cataloguePackageList) {
                if (catalogueItem.getSaleCode().equals(cataloguePackage.getSaleCode())) {
                    catalogueItem.getPackages().add(cataloguePackage);
                }
            }
        }
    }

    /**
     * Get a page by the page index
     *
     * @param pageIndex the index of the page to get for
     * @return the catalogue page
     */
    public CataloguePage getCataloguePage(String pageIndex) {
        for (CataloguePage cataloguePage : this.cataloguePageList) {
            if (cataloguePage.getNameIndex().equals(pageIndex)) {
                return cataloguePage;
            }
        }

        return null;
    }

    /**
     * Get an item by it's sale code.
     *
     * @param saleCode the item sale code identifier
     * @return the item, if successful
     */
    public CatalogueItem getCatalogueItem(String saleCode) {
        for (CatalogueItem catalogueItem : this.catalogueItemList) {
            if (catalogueItem.isHidden()) {
                continue;
            }

            if (catalogueItem.getSaleCode().equals(saleCode)) {
                return catalogueItem;
            }
        }

        return null;
    }

    /**
     * Get a list of items for the catalogue page.
     *
     * @param pageId the id of the page to get the items for
     * @return the list of items
     */
    public List<CatalogueItem> getCataloguePageItems(int pageId) {
        List<CatalogueItem> items = new ArrayList<>();

        for (CatalogueItem catalogueItem : this.catalogueItemList) {
            if (catalogueItem.isHidden()) {
                continue;
            }

            if (catalogueItem.getPageId() == pageId) {
                items.add(catalogueItem);
            }
        }

        return items;
    }

    /**
     * Get catalogue page list.
     *
     * @return the list of catalogue pages
     */
    public List<CataloguePage> getCataloguePages() {
        return this.cataloguePageList;
    }

    /**
     * Get catalogue page list for a certain rank
     *
     * @return the list of catalogue pages
     */
    public List<CataloguePage> getPagesForRank(PlayerRank minimumRank) {
        List<CataloguePage> cataloguePagesForRank = new ArrayList<>();

        for (CataloguePage page : this.cataloguePageList) {
            if (!page.isIndexVisible()) {
                continue;
            }

            if (minimumRank.getRankId() >= page.getMinRole().getRankId()) {
                cataloguePagesForRank.add(page);
            }
        }

        return cataloguePagesForRank;
    }

    /**
     * Get catalogue items list.
     *
     * @return the list of items packages
     */
    public List<CatalogueItem> getCatalogueItems() {
        return catalogueItemList;
    }

    /**
     * Get the {@link CatalogueManager} instance
     *
     * @return the catalogue manager instance
     */
    public static CatalogueManager getInstance() {
        if (instance == null) {
            instance = new CatalogueManager();
        }

        return instance;
    }

    /**
     * Resets the catalogue manager singleton.
     */
    public static void reset() {
        instance = null;
        CatalogueManager.getInstance();
    }
}
