package net.h4bbo.http.kepler.dao;

import org.alexdev.duckhttpd.util.config.Settings;
import net.h4bbo.kepler.dao.Storage;
import net.h4bbo.http.kepler.game.news.NewsArticle;
import net.h4bbo.http.kepler.game.news.NewsCategory;
import net.h4bbo.http.kepler.game.news.NewsDateKey;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class NewsDao {
    public static Map<String, List<NewsArticle>> getPastYear(boolean includeUnpublished, int filterCategoryId) {
        var monthlyNews = new LinkedHashMap<String, List<NewsArticle>>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT *, " +
                    "CONCAT(MONTHNAME(created_at), ' ', YEAR(created_at)) AS month_year, " +
                    "(SELECT GROUP_CONCAT(category_id SEPARATOR ',') FROM site_articles_categories " +
                    "WHERE article_id = site_articles.id GROUP BY article_id) AS categories FROM `site_articles` WHERE ");

            if (!includeUnpublished) {
                queryBuilder.append("is_published = 1 ");
            } else {
                queryBuilder.append("(is_published = 0 OR is_published = 1) ");
            }


            if (filterCategoryId > 0) {
                queryBuilder.append("AND ((SELECT COUNT(*) FROM site_articles_categories WHERE category_id = " + filterCategoryId + " AND article_id = id) > 0) ");

            }

            queryBuilder.append("ORDER BY created_at DESC");

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare(queryBuilder.toString(), sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String monthYear = resultSet.getString("month_year");

                if (!monthlyNews.containsKey(monthYear)) {
                    monthlyNews.put(monthYear, new LinkedList<>());
                }

                monthlyNews.get(monthYear).add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return monthlyNews;
    }

    public static Map<String, List<NewsArticle>> getArchive(boolean includeUnpublished) {
        var categorised = new LinkedHashMap<String, List<NewsArticle>>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT *, " +
                    "(SELECT label FROM article_categories WHERE article_categories.id = (SELECT category_id FROM site_articles_categories WHERE article_id = site_articles.id GROUP BY article_id LIMIT 1)) as category_name, " +
                    "(SELECT GROUP_CONCAT(category_id SEPARATOR ',') FROM site_articles_categories WHERE article_id = site_articles.id GROUP BY article_id) AS categories " +
                    "FROM `site_articles` WHERE ");

            if (!includeUnpublished) {
                queryBuilder.append("is_published = 1 ");
            } else {
                queryBuilder.append("(is_published = 0 OR is_published = 1) ");
            }

            queryBuilder.append("ORDER BY created_at DESC");

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare(queryBuilder.toString(), sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String categoryName = resultSet.getString("category_name");

                if (categoryName == null) {
                    continue;
                }

                if (!categorised.containsKey(categoryName)) {
                    categorised.put(categoryName, new LinkedList<>());
                }

                categorised.get(categoryName).add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return categorised;
    }

    public static List<NewsArticle> getTop(NewsDateKey dateKey, int limit, boolean includeUnpublished, List<String> excludeNews, int filterCategoryId) {
        List<NewsArticle> articles = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            Calendar c = Calendar.getInstance();

            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT *, " +
                    "(SELECT GROUP_CONCAT(category_id SEPARATOR ',') FROM site_articles_categories WHERE article_id = site_articles.id GROUP BY article_id) AS categories"
                    + " FROM `site_articles` WHERE ");

            if (!includeUnpublished) {
                queryBuilder.append("is_published = 1 ");
            } else {
                queryBuilder.append("(is_published = 0 OR is_published = 1) ");
            }

            if (dateKey == NewsDateKey.TODAY) {
                queryBuilder.append("AND YEAR(created_at) = YEAR(NOW()) AND MONTH(created_at) = MONTH(NOW()) AND DAY(created_at) = DAY(NOW()) ");
            }

            if (dateKey == NewsDateKey.YESTERDAY) {
                queryBuilder.append("AND DATE(created_at) =  SUBDATE(CURRENT_DATE(), INTERVAL 1 DAY) ");
            }

            if (dateKey == NewsDateKey.THIS_WEEK) {
                queryBuilder.append("AND UNIX_TIMESTAMP() < (UNIX_TIMESTAMP(created_at) + " + TimeUnit.DAYS.toSeconds(7) + ") ");
                //queryBuilder.append("AND YEARWEEK(created_at) = YEARWEEK(NOW()) ");
            }

            if (dateKey == NewsDateKey.THIS_MONTH) {
                queryBuilder.append("AND UNIX_TIMESTAMP() < (UNIX_TIMESTAMP(created_at) + " + TimeUnit.DAYS.toSeconds(c.getActualMaximum(Calendar.DAY_OF_MONTH)) + ") ");
                //queryBuilder.append("AND (YEAR(created_at) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) AND MONTH(created_at) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH)) ");
            }

            if (dateKey == NewsDateKey.PAST_YEAR) {
                queryBuilder.append("AND UNIX_TIMESTAMP() < (UNIX_TIMESTAMP(created_at) + " + TimeUnit.DAYS.toSeconds(c.getActualMaximum(Calendar.DAY_OF_YEAR)) + ") ");
                //queryBuilder.append("AND (created_at >= (NOW() - INTERVAL 12 MONTH)) ");
            }

            /*if (dateKey == NewsDateKey.THIS_YEAR) {
                queryBuilder.append("AND ((YEAR(created_at) = YEAR(NOW())) OR (YEAR(created_at) = YEAR(NOW()) - 1))");
            }*/


            /*if (dateKey == NewsDateKey.LAST_MONTH) {
                queryBuilder.append("AND YEAR(created_at) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) AND MONTH(created_at) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH)");
            }*/

            /*if (dateKey == NewsDateKey.LAST_WEEK) {
                queryBuilder.append("AND YEAR(created_at) = YEAR(CURRENT_DATE - INTERVAL 1 WEEK) AND MONTH(created_at) = MONTH(CURRENT_DATE - INTERVAL 1 WEEK)");
            }*/

            if (excludeNews.size() > 0) {
                queryBuilder.append("AND id NOT IN (" + String.join(",", excludeNews) + ") ");
            }

            if (filterCategoryId > 0) {
                queryBuilder.append("AND ((SELECT COUNT(*) FROM site_articles_categories WHERE category_id = " + filterCategoryId + " AND article_id = id) > 0) ");

            }

            queryBuilder.append("ORDER BY created_at DESC LIMIT " + limit);

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare(queryBuilder.toString(), sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                articles.add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return articles;
    }

    public static HashMap<NewsDateKey, List<NewsArticle>> getTop(int limit, boolean includeUnpublished, int filterCategoryId) {
        HashMap<NewsDateKey, List<NewsArticle>> articleMap = new HashMap<>();

        var newsToday = getTop(NewsDateKey.TODAY, limit, includeUnpublished, List.of(), filterCategoryId);
        var exclusionList = newsToday.stream()
                .map(article -> String.valueOf(article.getId()))
                .collect(Collectors.toList());

        var newsYesterday = getTop(NewsDateKey.YESTERDAY, limit, includeUnpublished, exclusionList, filterCategoryId);
        exclusionList.addAll(newsYesterday.stream()
                .map(article -> String.valueOf(article.getId()))
                .collect(Collectors.toList()));

        var newsThisWeek = getTop(NewsDateKey.THIS_WEEK, limit, includeUnpublished, exclusionList, filterCategoryId);
        exclusionList.addAll(newsThisWeek.stream()
                .map(article -> String.valueOf(article.getId()))
                .collect(Collectors.toList()));

        var newsThisMonth = getTop(NewsDateKey.THIS_MONTH, limit, includeUnpublished, exclusionList, filterCategoryId);
        exclusionList.addAll(newsThisMonth.stream()
                .map(article -> String.valueOf(article.getId()))
                .collect(Collectors.toList()));

        var newsPastYear = getTop(NewsDateKey.PAST_YEAR, limit, includeUnpublished, exclusionList, filterCategoryId);
        exclusionList.addAll(newsPastYear.stream()
                .map(article -> String.valueOf(article.getId()))
                .collect(Collectors.toList()));


        /*var newsThisYear = getTop(NewsDateKey.THIS_YEAR, limit, includeUnpublished, exclusionList, filterCategoryId);
        exclusionList.addAll(newsThisYear.stream()
                .map(article -> String.valueOf(article.getId()))
                .collect(Collectors.toList()));*/

        articleMap.put(NewsDateKey.TODAY, newsToday);
        articleMap.put(NewsDateKey.YESTERDAY, newsYesterday);
        articleMap.put(NewsDateKey.THIS_WEEK, newsThisWeek);
        articleMap.put(NewsDateKey.THIS_MONTH, newsThisMonth);
        articleMap.put(NewsDateKey.PAST_YEAR, newsPastYear);
        //articleMap.put(NewsDateKey.LAST_WEEK, newsLastWeek);
        //articleMap.put(NewsDateKey.LAST_MONTH, newsLastMonth);
        //articleMap.put(NewsDateKey.THIS_YEAR, newsThisYear);
        return articleMap;
    }

    public static Map<Integer, NewsCategory> getCategories() {
        var categories = new HashMap<Integer, NewsCategory>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM article_categories", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                categories.put(resultSet.getInt("id"), new NewsCategory(resultSet.getInt("id"), resultSet.getString("label"), resultSet.getString("category_index")));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return categories;
    }

    public static void insertCategories(int articleId, List<NewsCategory> newsCategories) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM site_articles_categories WHERE article_id = ?", sqlConnection);
            preparedStatement.setInt(1, articleId);
            preparedStatement.execute();

            for (var category : newsCategories) {
                preparedStatement = Storage.getStorage().prepare("INSERT INTO site_articles_categories (article_id, category_id) VALUES (?, ?)", sqlConnection);
                preparedStatement.setInt(1, articleId);
                preparedStatement.setInt(2, category.getId());
                preparedStatement.execute();
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static int create(String title, String shortstory, String fullstory, String topstory, String topstoryOverride, int authorId, String authorOverride, String category, String articleImage, long publishDate, boolean isFuturePublished, boolean isPublished) {
        int articleId = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO `site_articles` (title, author_id, author_override, short_story, full_story, created_at, topstory, topstory_override, article_image, is_future_published, is_published) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", sqlConnection);
            preparedStatement.setString(1, title);
            preparedStatement.setInt(2, authorId);
            preparedStatement.setString(3, authorOverride);
            preparedStatement.setString(4, shortstory);
            preparedStatement.setString(5, fullstory);
            preparedStatement.setTimestamp(6, new java.sql.Timestamp(publishDate * 1000L));
            preparedStatement.setString(7, topstory);
            preparedStatement.setString(8, topstoryOverride);
            preparedStatement.setString(9, articleImage);
            preparedStatement.setBoolean(10, isFuturePublished);
            preparedStatement.setBoolean(11, isPublished);
            preparedStatement.execute();
            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                articleId = resultSet.getInt(1);
            }
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return articleId;
    }

    public static boolean exists(int id) {
        boolean exists = false;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT id FROM site_articles WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                exists = true;
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return exists;
    }

    public static NewsArticle get(int id) {
        NewsArticle article = null;
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT *, (SELECT GROUP_CONCAT(category_id SEPARATOR ',') FROM site_articles_categories WHERE article_id = site_articles.id GROUP BY article_id) AS categories FROM site_articles WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                article = fill(resultSet);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return article;
    }

    public static void delete(int id) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM site_articles WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void publishFutureArticles() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE site_articles SET is_published = 1 WHERE CURRENT_TIMESTAMP() > created_at AND is_published = 0 AND is_future_published = 1", sqlConnection);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void save(NewsArticle article) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE site_articles SET title = ?, short_story = ?, full_story = ?, topstory = ?, article_image = ?, is_published = ?, created_at = ?, is_future_published = ?, author_override = ?, topstory_override = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, article.getTitle());
            preparedStatement.setString(2, article.getShortStory());
            preparedStatement.setString(3, article.getFullStory());
            preparedStatement.setString(4, article.getTopStory());
            preparedStatement.setString(5, article.getArticleImage());
            preparedStatement.setBoolean(6, article.isPublished());
            preparedStatement.setTimestamp(7, new java.sql.Timestamp(article.getTimestamp() * 1000L));
            preparedStatement.setBoolean(8, article.isFuturePublished());
            preparedStatement.setString(9, article.getAuthorOverride());
            preparedStatement.setString(10, article.getTopstoryOverride());
            preparedStatement.setInt(11, article.getId());
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static List<String> getTopStoryImages() {
        List<String> images = new ArrayList<String>();

        for (File file : Objects.requireNonNull(Paths.get(Settings.getInstance().getSiteDirectory(), "c_images", "Top_Story_Images").toFile().listFiles())) {
            if (!file.getName().contains(".gif")) {
                continue;
            }

            images.add(file.getName());
        }

        Collections.sort(images);
        return images;
    }

    private static NewsArticle fill(ResultSet resultSet) throws SQLException {
        return new NewsArticle(
                resultSet.getInt("id"), resultSet.getString("title"), resultSet.getInt("author_id"), resultSet.getString("author_override"),
                resultSet.getString("short_story"), resultSet.getString("full_story"), resultSet.getTime("created_at").getTime() / 1000L,
                resultSet.getString("topstory"), resultSet.getString("topstory_override"), resultSet.getString("article_image"), resultSet.getString("categories"),
                resultSet.getBoolean("is_published"), resultSet.getInt("views"), resultSet.getBoolean("is_future_published"));
    }
}
