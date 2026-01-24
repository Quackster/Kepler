package net.h4bbo.http.kepler.game.news;

import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.util.HousekeepingUtil;

import java.util.ArrayList;
import java.util.List;

public class NewsArticle {
    private int id;
    private String title;
    private int authorId;
    private String authorOverride;
    private String shortstory;
    private String fullstory;
    private long timestamp;
    private String topstory;
    private String topstoryOverride;
    private String articleImage;
    private List<NewsCategory> categories;
    private boolean isPublished;
    private int views;
    private boolean isFuturePublished;

    public NewsArticle(int id, String title, int authorId, String authorOverride, String shortstory, String fullStory, long date, String topstory, String topstoryOverride, String articleImage, String categories, boolean isPublished, int views, boolean isFuturePublished) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.authorOverride = authorOverride;
        this.shortstory = shortstory;
        this.fullstory = fullStory;
        this.timestamp = date;
        this.articleImage = articleImage;
        this.topstory = topstory;
        this.topstoryOverride = topstoryOverride;
        this.categories = parseCategories(categories);
        this.isPublished = isPublished;
        this.views = views;
        this.isFuturePublished = isFuturePublished;
    }

    private List<NewsCategory> parseCategories(String categories) {
        var categoryList = new ArrayList<NewsCategory>();

        if (categories != null && categories.length() > 0) {
            for (String categoryData : categories.split(",")) {
                var category = NewsManager.getInstance().getCategoryById(Integer.parseInt(categoryData));

                if (category != null) {
                    categoryList.add(category);
                }
            }
        }

        return categoryList;
    }

    public boolean hasCategory(int id) {
        return this.categories.stream().anyMatch(category -> category.getId() == id);
    }

    public String getAuthor() {
        if (this.authorOverride.length() > 0)
            return this.authorOverride;

        return PlayerDao.getName(this.authorId);
    }

    public int getAuthorId() {
        return authorId;
    }

    public String getUrl() {
        if (this.id == 0) {
            return "0-no-news";
        }

        String newTitle = this.title;
        newTitle = newTitle.replace("!", "");
        newTitle = newTitle.replace("'", "");
        newTitle = newTitle.replace("@", "");
        newTitle = newTitle.replace("&", "");
        newTitle = newTitle.replace("*", "");
        newTitle = newTitle.replace("%", "");
        newTitle = newTitle.replace("[", "");
        newTitle = newTitle.replace("]", "");
        newTitle = newTitle.replace("#", "");
        newTitle = newTitle.replace("=", "");
        newTitle = newTitle.replace("\"", "");
        newTitle = newTitle.replace(":", "");
        newTitle = newTitle.replace(">", "");
        newTitle = newTitle.replace("<", "");
        newTitle = newTitle.replace(",", "");
        newTitle = newTitle.replace(".", "");
        newTitle = newTitle.replace("+", "");
        newTitle = newTitle.replace("-", "");
        newTitle = newTitle.replace("=", "");
        newTitle = newTitle.replace("_", "");
        newTitle = newTitle.replace("/", "");
        newTitle = newTitle.replace("?", "");
        newTitle = newTitle.replace("\\", "");
        newTitle = newTitle.replace(" ", "-");
        newTitle = newTitle.toLowerCase();

        return this.id + "-" + newTitle;
    }

    /**
     * Get id of article
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Get title of news article
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set title of news article
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortStory() {
        return shortstory;
    }

    public void setShortStory(String shortstory) {
        this.shortstory = shortstory;
    }

    public String getDate() {
        return DateUtil.getDate(this.timestamp, "EEE dd MMM, yyyy").replace(".", "");
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLiveTopStory() {
        if (this.topstoryOverride.length() > 0) {
            return this.topstoryOverride;
        }

        //{{ site.staticContentPath }}/c_images/Top_Story_Images/{{ article1.getTopStory() }}

        return GameConfiguration.getInstance().getString("static.content.path") + "/c_images/Top_Story_Images/" + topstory;
    }

    public String getTopStory() {
        return topstory;
    }

    public void setTopStory(String topstory) {
        this.topstory = topstory;
    }

    public String getFullStory() {
        return this.fullstory;
    }

    public String getEscapedStory() { return new HousekeepingUtil().formatNewsStory(this.fullstory);
    }

    public void setFullStory(String fullstory) {
        this.fullstory = fullstory;
    }

    public List<NewsCategory> getCategories() {
        return categories;
    }

    public String getArticleImage() {
        return articleImage;
    }

    public void setArticleImage(String articleImage) {
        this.articleImage = articleImage;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public boolean isFuturePublished() {
        return isFuturePublished;
    }

    public void setFuturePublished(boolean futurePublished) {
        isFuturePublished = futurePublished;
    }

    public String getAuthorOverride() {
        return authorOverride;
    }

    public void setAuthorOverride(String authorOverride) {
        this.authorOverride = authorOverride;
    }

    public String getTopstoryOverride() {
        return topstoryOverride;
    }

    public void setTopstoryOverride(String topstoryOverride) {
        this.topstoryOverride = topstoryOverride;
    }
}
