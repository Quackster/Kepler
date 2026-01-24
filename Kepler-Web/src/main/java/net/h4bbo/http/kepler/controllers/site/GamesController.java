package net.h4bbo.http.kepler.controllers.site;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.HighscoreDao;
import net.h4bbo.kepler.game.games.enums.GameType;
import net.h4bbo.http.kepler.util.XSSUtil;
import org.apache.commons.lang3.StringUtils;

public class GamesController {
    private static final int HIGHSCORES_LIMIT = 10;

    public static void games(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        var template = webConnection.template("games");

        if (!webConnection.session().contains("highscoreGameId")) {
            webConnection.session().set("highscoreGameId", "1");
        }

        GameType gameType = GameType.BATTLEBALL;
        int gameId = webConnection.session().getInt("highscoreGameId");

        if (gameId == 1) {
            gameType = GameType.BATTLEBALL;
        }

        if (gameId == 2) {
            gameType = GameType.SNOWSTORM;
        }

        if (gameId == 0) {
            gameType = GameType.WOBBLE_SQUABBLE;
        }

        webConnection.session().set("gameScoreViewMonthly", true);

        webConnection.session().set("page", "games");
        appendpersonalhighscores(template, gameType, 1, gameId, webConnection.session().getBoolean("gameScoreViewMonthly"));
        template.render();
    }

    public static void games_all_time(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        var template = webConnection.template("games");

        if (!webConnection.session().contains("highscoreGameId")) {
            webConnection.session().set("highscoreGameId", "1");
        }

        GameType gameType = GameType.BATTLEBALL;
        int gameId = webConnection.session().getInt("highscoreGameId");

        if (gameId == 1) {
            gameType = GameType.BATTLEBALL;
        }

        if (gameId == 2) {
            gameType = GameType.SNOWSTORM;
        }

        if (gameId == 0) {
            gameType = GameType.WOBBLE_SQUABBLE;
        }

        webConnection.session().set("gameScoreViewMonthly", false);

        webConnection.session().set("page", "games");
        appendpersonalhighscores(template, gameType, 1, gameId, webConnection.session().getBoolean("gameScoreViewMonthly"));
        template.render();
    }

    private static void appendpersonalhighscores(Template template, GameType gameType, int pageNumber, int gameId, boolean viewMonthly) {
        template.set("scoreEntries", HighscoreDao.getScores(GamesController.HIGHSCORES_LIMIT, gameType, pageNumber, viewMonthly));
        template.set("gameId", gameId);
        template.set("pageNumber", pageNumber);
        template.set("viewMonthlyScores", viewMonthly);

        boolean hasNextPage = false;

        if (HighscoreDao.getScores(GamesController.HIGHSCORES_LIMIT, gameType, pageNumber + 1, viewMonthly).size() > 0) {
            hasNextPage = true;
        }

        template.set("hasNextPage", hasNextPage);
    }

    public static void personalhighscores(WebConnection webConnection) {
        var template = webConnection.template("habblet/personalhighscores");

        int pageNumber = 1;

        if (webConnection.post().contains("pageNumber") && StringUtils.isNumeric(webConnection.post().getString("pageNumber"))) {
            pageNumber =  webConnection.post().getInt("pageNumber");

            if (pageNumber < 1) {
                pageNumber = 1;
            }
        }

        int gameId = 1;
        GameType gameType = GameType.BATTLEBALL;

        if (webConnection.post().contains("gameId") && StringUtils.isNumeric(webConnection.post().getString("gameId"))) {
            gameId =  webConnection.post().getInt("gameId");

            if (gameId == 1) {
                gameType = GameType.BATTLEBALL;
            }

            if (gameId == 2) {
                gameType = GameType.SNOWSTORM;
            }

            if (gameId == 0) {
                gameType = GameType.WOBBLE_SQUABBLE;
            }

            webConnection.session().set("highscoreGameId", String.valueOf(gameId));
        }

        appendpersonalhighscores(template, gameType, pageNumber, gameId, webConnection.session().getBoolean("gameScoreViewMonthly"));
        template.render();
    }

}
