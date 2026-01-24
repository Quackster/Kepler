package net.h4bbo.http.kepler.game.news;

import net.h4bbo.http.kepler.dao.NewsDao;

import java.util.*;

public class NewsManager {
    private Map<Integer, NewsCategory> newsCategoryMap;
    private static NewsManager instance;

    public NewsManager() {
        this.newsCategoryMap = NewsDao.getCategories();
    }

    /**
     * Get category by ID.
     * @param categoryId the category id.
     * @return the category, if successful
     */
    public NewsCategory getCategoryById(int categoryId) {
        return this.newsCategoryMap.getOrDefault(categoryId, null);
    }

    /**
     * Get category by label.
     * @param categoryLabel the category id.
     * @return the category, if successful
     */
    public NewsCategory getCategoryByLabel(String categoryLabel) {
        return this.newsCategoryMap.values().stream().filter(article -> article.getIndex().equalsIgnoreCase(categoryLabel)).findFirst().orElse(null);
    }

    /**
     * Get categories
     *
     * @return the list of categories
     */
    public List<NewsCategory> getCategories() {
        List<NewsCategory> categories = new ArrayList<>(this.newsCategoryMap.values());
        categories.sort(Comparator.comparing(NewsCategory::getLabel));
        return categories;
    }

    /**
     * Get instance of {@link NewsManager}
     *
     * @return the manager instance
     */
    public static NewsManager getInstance() {
        if (instance == null) {
            instance = new NewsManager();
        }

        return instance;
    }

}
