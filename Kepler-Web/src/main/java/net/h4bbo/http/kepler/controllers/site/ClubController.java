package net.h4bbo.http.kepler.controllers.site;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.PlayerStatisticsDao;
import net.h4bbo.kepler.game.catalogue.CatalogueManager;
import net.h4bbo.kepler.game.club.ClubSubscription;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;
import net.h4bbo.kepler.game.player.statistics.PlayerStatisticManager;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.http.kepler.util.XSSUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClubController {
    public static void club(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        webConnection.session().set("page", "credits");
        renderclub(webConnection);
    }

    public static void renderclub(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        var template = webConnection.template("club");

        if (webConnection.session().getBoolean("authenticated")) {
            var playerDetails = (PlayerDetails) template.get("playerDetails");
            var statistics = new PlayerStatisticManager(playerDetails.getId(), PlayerStatisticsDao.getStatistics(playerDetails.getId()));
            ClubSubscription.countMemberDays(playerDetails, statistics);
        }

        for (int i = 0; i < 3; i++) {
            var choiceData = ClubSubscription.getChoiceData(i + 1);

            template.set("clubChoiceCredits" + (i + 1), choiceData.getKey());
            template.set("clubChoiceDays" + (i + 1), choiceData.getValue());
        }

        if (webConnection.session().getBoolean("authenticated")) {
            PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");
            PlayerStatisticManager statisticManager = new PlayerStatisticManager(playerDetails.getId(), PlayerStatisticsDao.getStatistics(playerDetails.getId()));

            if (playerDetails.hasClubSubscription()) {
                template.set("hcDays", TimeUnit.SECONDS.toDays(playerDetails.getClubExpiration() - DateUtil.getCurrentTimeSeconds()));

                int days = (int) TimeUnit.SECONDS.toDays(statisticManager.getLongValue(PlayerStatistic.CLUB_MEMBER_TIME));
                int sinceMonths = days > 0 ? days / 31 : 0;//(int) (now - player.getDetails().getFirstClubSubscription()) / 60 / 60 / 24 / 31;

                template.set("hcSinceMonths", sinceMonths);
            }
        }

        appendgiftdata(template, webConnection.session().contains("lastClubGiftMonth") ? webConnection.session().getInt("lastClubGiftMonth") : 1, 0, webConnection);
        template.render();
    }


    public static void habboClubGift(WebConnection connection) {
        if (!connection.post().contains("month") ||
            !connection.post().contains("catalogpage")) {
            connection.send("");
            return;
        }

        if (!StringUtils.isNumeric(connection.post().getString("month")) ||
            !StringUtils.isNumeric(connection.post().getString("catalogpage"))) {
            connection.send("");
            return;
        }

        int month = connection.post().getInt("month");
        int catalogpage = connection.post().getInt("catalogpage");

        var template = connection.template("habblet/habboclubgift");
        appendgiftdata(template, month, catalogpage, connection);
        template.render();
    }

    private static void appendgiftdata(Template template, int month, int catalogpage, WebConnection connection) {
        XSSUtil.clear(connection);

        var giftOrder = new ArrayList<>(Arrays.asList(ClubSubscription.getGiftOrder()));
        giftOrder.add(0, "club_sofa");

        int position = month - 1;

        if (position >= giftOrder.size()) {
            position = 0;
        }

        var nextSpriteGift = giftOrder.get(0);

        try {
            nextSpriteGift = giftOrder.get(position);
        } catch (Exception ex) { }

        List<Integer> pages = new ArrayList<>();

        catalogpage = 0;

        if (month >= 5 && month <= 8) {
            catalogpage = 1;
        }

        if (month >= 9 && month <= 12) {
            catalogpage = 2;
        }

        if (month >= 13 && month <= 16) {
            catalogpage = 3;
        }

        if (month >= 17 && month <= 20) {
            catalogpage = 4;
        }

        if (month >= 21 && month <= 23) {
            catalogpage = 5;
        }

        if (catalogpage == 0) {
            pages.add(1);
            pages.add(2);
            pages.add(3);
            pages.add(4);
            pages.add(5);
        }

        if (catalogpage == 1) {
            pages.add(5);
            pages.add(6);
            pages.add(7);
            pages.add(8);
            pages.add(9);
        }

        if (catalogpage == 2) {
            pages.add(9);
            pages.add(10);
            pages.add(11);
            pages.add(12);
            pages.add(13);
        }

        if (catalogpage == 3) {
            pages.add(13);
            pages.add(14);
            pages.add(15);
            pages.add(16);
            pages.add(17);
        }

        if (catalogpage == 4) {
            pages.add(17);
            pages.add(18);
            pages.add(19);
            pages.add(20);
            pages.add(21);
        }

        if (catalogpage == 5) {
            pages.add(19);
            pages.add(20);
            pages.add(21);
            pages.add(22);
            pages.add(23);
        }

        var definition = CatalogueManager.getInstance().getCatalogueItem(nextSpriteGift);

        template.set("pages", pages);
        template.set("currentPage", month);
        template.set("lastPage", giftOrder.size());

        if (definition.getDefinition() == null) {
            template.set("item", definition.getPackages().get(0).getDefinition());
        } else {
            template.set("item", definition.getDefinition());
        }

        connection.session().set("lastClubGiftMonth", month);
    }

    public static void clubTryout(WebConnection webConnection) {
        var template = webConnection.template("club_tryout");

        for (int i = 0; i < 3; i++) {
            var choiceData = ClubSubscription.getChoiceData(i + 1);

            template.set("clubChoiceCredits" + (i + 1), choiceData.getKey());
            template.set("clubChoiceDays" + (i + 1), choiceData.getValue());
        }

        if (webConnection.session().getBoolean("authenticated")) {
            PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");
            PlayerStatisticManager statisticManager = new PlayerStatisticManager(playerDetails.getId(), PlayerStatisticsDao.getStatistics(playerDetails.getId()));

            if (playerDetails.hasClubSubscription()) {
                template.set("hcDays", TimeUnit.SECONDS.toDays(playerDetails.getClubExpiration() - DateUtil.getCurrentTimeSeconds()));

                int days = (int) TimeUnit.SECONDS.toDays(statisticManager.getLongValue(PlayerStatistic.CLUB_MEMBER_TIME));
                int sinceMonths = days > 0 ? days / 31 : 0;//(int) (now - player.getDetails().getFirstClubSubscription()) / 60 / 60 / 24 / 31;

                template.set("hcSinceMonths", sinceMonths);
            }

            template.set("figure", playerDetails.getFigure());
            template.set("sex", playerDetails.getSex());
        }

        webConnection.session().set("page", "credits");
        template.render();
    }
}
