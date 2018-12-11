package org.alexdev.kepler.game.navigator;

import org.alexdev.kepler.dao.mysql.NavigatorDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NavigatorManager {
    private static NavigatorManager instance;
    private final HashMap<Integer, NavigatorCategory> categoryMap;

    private NavigatorManager() {
        this.categoryMap = NavigatorDao.getCategories();
    }

    /**
     * Get all categories by the parent id.
     *
     * @param parentId the parent id of the categories
     * @return the list of categories
     */
    public List<NavigatorCategory> getCategoriesByParentId(int parentId) {
        List<NavigatorCategory> categories =  new ArrayList<>();

        for (NavigatorCategory category : this.categoryMap.values()) {
            if (category.getParentId() == parentId) {
                categories.add(category);
            }
        }

        return categories;
    }

    /**
     * Get the {@link NavigatorCategory} by id
     *
     * @param categoryId the id of the category
     * @return the category instance
     */
    public NavigatorCategory getCategoryById(int categoryId) {
        if (this.categoryMap.containsKey(categoryId)) {
            return this.categoryMap.get(categoryId);
        }

        return null;
    }

    /**
     * Get the map of navigator categories
     *
     * @return the list of categories
     */
    public HashMap<Integer, NavigatorCategory> getCategories() {
        return this.categoryMap;
    }

    /**
     * Get instance of {@link NavigatorManager}
     *
     * @return the manager instance
     */
    public static NavigatorManager getInstance() {
        if (instance == null) {
            instance = new NavigatorManager();
        }

        return instance;
    }
}
