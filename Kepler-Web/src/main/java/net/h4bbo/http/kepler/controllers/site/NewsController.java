package net.h4bbo.http.kepler.controllers.site;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.http.kepler.dao.NewsDao;
import net.h4bbo.http.kepler.game.news.*;
import net.h4bbo.http.kepler.util.XSSUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewsController {
    public static void articles(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        webConnection.session().set("page", "community");

        var template = webConnection.template("news_articles");
        template.set("newsPage", "news");
        rendernews(webConnection, template, null);
        template.render();
    }

    public static void fansites(WebConnection webConnection) {
        XSSUtil.clear(webConnection);
        webConnection.session().set("page", "community");

        var template = webConnection.template("news_articles");
        template.set("newsPage", "fansites");
        rendernews(webConnection, template, NewsManager.getInstance().getCategoryByLabel("fansites"));
        template.render();
    }

    public static void events(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        webConnection.session().set("page", "community");

        var template = webConnection.template("news_articles");
        template.set("newsPage", "events");
        rendernews(webConnection, template, NewsManager.getInstance().getCategoryByLabel("events"));
        template.render();
    }

    private static void rendernews(WebConnection webConnection, Template template, NewsCategory override) {
        int newsArticleId = 0;

        try {
            newsArticleId = Integer.parseInt(webConnection.getMatches().get(0));
        } catch (Exception ignored) {
        }

        int filterCategoryId = (override != null ? override.getId() : 0);
        boolean hasArchive = false;

        /*if (filterCategoryId > 0) {
            var categoryLabel = webConnection.getMatches().get(0);
            var category = NewsManager.getInstance().getCategoryByLabel(categoryLabel);

            if (category != null) {
                filterCategoryId = category.getId();
            }

            template.set("urlSuffix", "");
            hasArchive = true;
        }*/

        NewsView view = NewsView.DEFAULT;

        template.set("monthlyView", false);
        template.set("archiveView", false);

        template.set("archives", new HashMap<String, List<NewsArticle>>());;
        template.set("months", new HashMap<String, List<NewsArticle>>());
        template.set("articlesToday", new ArrayList<>());
        template.set("articlesYesterday", new ArrayList<>());
        template.set("articlesThisWeek", new ArrayList<>());
        template.set("articlesThisMonth", new ArrayList<>());
        template.set("articlesPastYear", new ArrayList<>());

        if (webConnection.getRouteRequest().endsWith("archive") ||
            webConnection.getRouteRequest().endsWith("archive/")) {
            template.set("urlSuffix", "/in/archive");
            template.set("archiveView", true);
            view = NewsView.ARCHIVE;

        } else if (filterCategoryId > 0 || webConnection.getRouteRequest().contains("/category/")) {
            if (webConnection.getRouteRequest().contains("/category/")) {
                override = NewsManager.getInstance().getCategoryByLabel(webConnection.getMatches().get(0));
            }

            if (override != null) {
                filterCategoryId = override.getId();
                view = NewsView.MONTHS;
                template.set("monthlyView", true);
            }

        } else {
            template.set("urlSuffix", "");
        }

        boolean includeUnpublished = template.get("playerDetails") != null && ((PlayerDetails)template.get("playerDetails")).getRank().getRankId() > 1;
        NewsArticle newsArticle = null;

        if (!NewsDao.exists(newsArticleId)) {
            try {
                var articles = NewsDao.getTop(NewsDateKey.ALL, 1, includeUnpublished, List.of(), filterCategoryId);

                if (articles.size() > 0) {
                    newsArticle = articles.get(0);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            newsArticle = NewsDao.get(newsArticleId);
        }


        if (newsArticle == null || (!newsArticle.isPublished() && !includeUnpublished)) {
            newsArticle = new NewsArticle(1,
                    "No news",
                    -1,
                    "Hotel Staff",
                    "",
                    "There is no news.",
                    System.currentTimeMillis() / 1000L,
                    "",
                    "",
                    "",
                    "",
                    true, 0,false);
            //webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
            //return;
        }

        template.set("currentArticle", newsArticle);

        if (view == NewsView.ARCHIVE) {
            var monthlyArticles = NewsDao.getArchive(includeUnpublished);
            template.set("archives", monthlyArticles);
            template.set("archiveView", true);
        }

        if (view == NewsView.MONTHS) {
            var monthlyArticles = NewsDao.getPastYear(includeUnpublished, filterCategoryId);
            template.set("months", monthlyArticles);
            template.set("monthlyView", true);
        }

        if (view == NewsView.DEFAULT) {
            var articleList = NewsDao.getTop(Integer.MAX_VALUE, includeUnpublished, filterCategoryId);
            template.set("articlesToday", articleList.get(NewsDateKey.TODAY));
            template.set("articlesYesterday", articleList.get(NewsDateKey.YESTERDAY));
            template.set("articlesThisWeek", articleList.get(NewsDateKey.THIS_WEEK));
            template.set("articlesThisMonth", articleList.get(NewsDateKey.THIS_MONTH));
            template.set("articlesPastYear", (articleList.get(NewsDateKey.TODAY).isEmpty() && articleList.get(NewsDateKey.YESTERDAY).isEmpty() && articleList.get(NewsDateKey.THIS_WEEK).isEmpty() && articleList.get(NewsDateKey.THIS_MONTH).isEmpty()) ?
                    articleList.get(NewsDateKey.PAST_YEAR) :
                    List.of());
            //template.set("articlesThisYear", hasArchive ? articleList.get(NewsDateKey.THIS_YEAR) : List.of());
        }

        //template.set("articles", articleList);
    }
}
