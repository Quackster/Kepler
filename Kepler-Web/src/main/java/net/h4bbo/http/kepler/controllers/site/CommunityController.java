package net.h4bbo.http.kepler.controllers.site;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.http.kepler.game.news.NewsArticle;
import net.h4bbo.http.kepler.server.Watchdog;
import net.h4bbo.http.kepler.util.XSSUtil;

import java.util.List;

public class CommunityController {
    public static void community(WebConnection webConnection) {
        XSSUtil.clear(webConnection);
        var template = webConnection.template("community");

        /*
        if (CacheManager.useCachePage(webConnection, "community")) {
            webConnection.send(CacheManager.getPage(webConnection, "community"));
            return;
        }
        */
        //boolean includeUnpublished = template.get("playerDetails") != null && ((PlayerDetails)template.get("playerDetails")).getRank().getRankId() > 1;
        NewsArticle[] articles = new NewsArticle[5];

        int i = 0;
        List<NewsArticle> articleList = /*includeUnpublished ? Watchdog.NEWS_STAFF : */Watchdog.NEWS;

        for (var article : articleList) {
            articles[i++] = article;
        }

        for (i = 0; i < 5; i++) {
            if (articles[i] == null) {
                articles[i] = new NewsArticle(0,"No news", 0, "", "","", DateUtil.getCurrentTimeSeconds(), "attention_topstory.png", "", "", "0",  true, 0, false);
            }

            template.set("article" + (i + 1), articles[i]);
        }

        webConnection.session().set("page", "community");

        template.set("recommendedRooms", Watchdog.RECOMMENDED_ROOMS);
        template.set("hiddenRecommendedRooms", Watchdog.HIDDEN_RECOMMENDED_ROOMS);
        template.set("randomHabbos", PlayerDao.getRandomHabbos(18));
        template.set("tagCloud", Watchdog.TAG_CLOUD_10);
        template.set("recentTopics", Watchdog.RECENT_DISCUSSIONS);
        template.set("recentHiddenTopics", Watchdog.NEXT_RECENT_DISCUSSIONS);

        template.render();

        //CacheManager.savePage(webConnection, "community", ((TwigTemplate)template).renderHTML(), 15);
        //webConnection.send(CacheManager.getPage(webConnection, "community"));
    }

}
