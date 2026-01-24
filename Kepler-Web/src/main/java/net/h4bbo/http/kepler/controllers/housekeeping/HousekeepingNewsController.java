package net.h4bbo.http.kepler.controllers.housekeeping;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.http.kepler.Routes;
import net.h4bbo.http.kepler.dao.NewsDao;
import net.h4bbo.http.kepler.game.housekeeping.HousekeepingManager;
import net.h4bbo.http.kepler.game.news.NewsArticle;
import net.h4bbo.http.kepler.game.news.NewsCategory;
import net.h4bbo.http.kepler.game.news.NewsDateKey;
import net.h4bbo.http.kepler.game.news.NewsManager;
import net.h4bbo.http.kepler.util.HousekeepingUtil;
import net.h4bbo.http.kepler.util.SessionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class HousekeepingNewsController {
    private static final int MAX_NEWS_TO_DISPLAY = 250;

    /**
     * Handle the /housekeeping/articles URI request
     *
     * @param client the connection
     */
    public static void articles(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        Template tpl = client.template("housekeeping/articles");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "articles/create")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        tpl.set("pageName", "View News");
        tpl.set("articles", NewsDao.getTop(NewsDateKey.ALL, MAX_NEWS_TO_DISPLAY, true, List.of(), 0));
        tpl.render();

        // Delete alert after it's been rendered
        client.session().delete("alertMessage");
    }

    /**
     * Handle the /housekeeping/articles/create URI request
     *
     * @param client the connection
     */
    public static void create(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        PlayerDetails session = PlayerDao.getDetails(client.session().getInt("user.id"));

        if (!HousekeepingManager.getInstance().hasPermission(session.getRank(), "articles/create")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        if (client.post().queries().size() > 0) {
            long publishDate = DateUtil.getFromFormat("yyyy-MM-dd'T'HH:mm", client.post().getString("datePublished"));

            List<NewsCategory> categories = new ArrayList<>();

            for (String data : client.post().getArray("categories[]")) {
                var category = NewsManager.getInstance().getCategoryByLabel(data);

                if (category != null) {
                    categories.add(category);
                }
            }

            int articleId = NewsDao.create(
                    client.post().getString("title"),
                    client.post().getString("shortstory"),
                    client.post().getString("fullstory"),
                    client.post().getString("topstory"),
                    client.post().getString("topstoryOverride"),
                    session.getId(),
                    client.post().getString("authorOverride"),
                    client.post().getString("category"),
                    client.post().getString("articleimage"),
                    publishDate,
                    client.post().getString("futurePublished").equals("true"),
                    client.post().getString("published").equals("true")
            );

            NewsDao.insertCategories(articleId, categories);

            client.session().set("alertColour", "success");
            client.session().set("alertMessage", "The submission of the news article was successful");
            client.redirect("/" + Routes.HOUSEKEEPING_PATH + "/articles");
            return;
        }

        List<String> images = NewsDao.getTopStoryImages();

        Template tpl = client.template("housekeeping/articles_create");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());
        tpl.set("pageName", "Create News");
        tpl.set("images", images);
        tpl.set("randomImage", images.get(ThreadLocalRandom.current().nextInt(images.size())));
        tpl.set("currentDate", DateUtil.getDate(DateUtil.getCurrentTimeSeconds(), "yyyy-MM-dd'T'HH:mm"));
        tpl.set("categories", NewsManager.getInstance().getCategories());
        tpl.render();

        // Delete alert after it's been rendered
        client.session().delete("alertMessage");
    }

    /**
     * Handle the /housekeeping/articles/delete URI request
     *
     * @param client the connection
     */
    public static void delete(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        NewsArticle article = NewsDao.get(client.get().getInt("id"));

        Template tpl = client.template("housekeeping/articles");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (article.getAuthorId() != playerDetails.getId()) {
            if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "articles/delete_any")) {
                client.redirect("/" + Routes.HOUSEKEEPING_PATH);
                return;
            }
        }

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "articles/delete_own")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        if (!client.get().contains("id")) {
            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "There was no article selected to delete");
        } else if (!NewsDao.exists(client.get().getInt("id"))) {
            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "The article does not exist");
        } else {
            client.session().set("alertColour", "success");
            client.session().set("alertMessage", "Successfully deleted the article");
            NewsDao.delete(client.get().getInt("id"));
        }


        tpl.set("pageName", "Delete News");
        tpl.set("articles", NewsDao.getTop(NewsDateKey.ALL, MAX_NEWS_TO_DISPLAY, true, List.of(), 0));
        tpl.render();

        // Delete alert after it's been rendered
        client.session().delete("alertMessage");

    }

    /**
     * Handle the /housekeeping/articles/edit URI request
     *
     * @param client the connection
     */
    public static void edit(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        Template tpl = client.template("housekeeping/articles_edit");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "articles/edit_own")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        tpl.set("images", NewsDao.getTopStoryImages());

        if (!client.get().contains("id")) {
            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "There was no article selected to edit");
        } else if (!NewsDao.exists(client.get().getInt("id"))) {
            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "The article does not exist");
        } else {
            NewsArticle article = NewsDao.get(client.get().getInt("id"));

            if (article.getAuthorId() != playerDetails.getId()) {
                if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "articles/edit_any")) {
                    client.redirect("/" + Routes.HOUSEKEEPING_PATH);
                    return;
                }
            }

            if (client.post().queries().size() > 0) {
                long publishDate = DateUtil.getFromFormat("yyyy-MM-dd'T'HH:mm", client.post().getString("datePublished"));

                List<NewsCategory> categories = new ArrayList<>();

                for (String data : client.post().getArray("categories[]")) {
                    var category = NewsManager.getInstance().getCategoryByLabel(data);

                    if (category != null) {
                        categories.add(category);
                    }
                }

                NewsDao.insertCategories(article.getId(), categories);

                article.setTitle(client.post().getString("title"));
                article.setShortStory(client.post().getString("shortstory"));
                article.setFullStory(client.post().getString("fullstory"));
                article.setTopStory(client.post().getString("topstory"));
                article.setTopstoryOverride(client.post().getString("topstoryOverride"));
                article.setAuthorOverride(client.post().getString("authorOverride"));
                article.setArticleImage(client.post().getString("articleimage"));
                article.setPublished(client.post().getString("published").equals("true"));
                article.setFuturePublished(client.post().getString("futurePublished").equals("true"));
                article.setTimestamp(publishDate);

                article.getCategories().clear();
                article.getCategories().addAll(categories);

                NewsDao.save(article);

                client.session().set("alertColour", "success");
                client.session().set("alertMessage", "The article was successfully saved");
            }

            tpl.set("currentDate", DateUtil.getDate(article.getTimestamp(), "yyyy-MM-dd'T'HH:mm"));
            tpl.set("article", article);
            tpl.set("categories", NewsManager.getInstance().getCategories());
        }

        tpl.render();

        // Delete alert after it's been rendered
        client.session().delete("alertMessage");
    }

    public static void preview_news_article(WebConnection webConnection) {
        if (!webConnection.post().contains("body")) {
            webConnection.send("");
            return;
        }

        webConnection.send(new HousekeepingUtil().formatNewsStory(webConnection.post().getString("body")));
    }
}
