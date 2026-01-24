package net.h4bbo.http.kepler.controllers.habblet;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.dao.mysql.AlertsDao;
import net.h4bbo.kepler.dao.mysql.PlayerStatisticsDao;
import net.h4bbo.kepler.game.alerts.AccountAlert;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;
import net.h4bbo.kepler.game.player.statistics.PlayerStatisticManager;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.http.kepler.game.account.BeginnerGiftManager;

import java.util.List;

public class FeedController {
    public static void removeFeedItem(WebConnection connection) {
        if (!connection.session().getBoolean("authenticated")) {
            connection.redirect("/");
            return;
        }

        int feedItemIndex = -1;

        try {
            feedItemIndex = connection.post().getInt("feedItemIndex");
        } catch (Exception ex) {

        }

        if (feedItemIndex != -1) {
            int userId = connection.session().getInt("user.id");
            List<AccountAlert> accountAlerts = AlertsDao.getAlerts(userId);

            try {
                AccountAlert alert = accountAlerts.get(feedItemIndex);
                AlertsDao.deleteAlert(userId, alert.getId());
            } catch (Exception ex) {

            }

            connection.send("");
        }
    }

    public static void nextgift(WebConnection connection) {
        if (!connection.session().getBoolean("authenticated")) {
            connection.send("");
            return;
        }

        var template = connection.template("habblet/nextgift");

        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");
        var statistics = new PlayerStatisticManager(playerDetails.getId(), PlayerStatisticsDao.getStatistics(playerDetails.getId()));

        template.set("newbieRoomLayout", statistics.getIntValue(PlayerStatistic.NEWBIE_ROOM_LAYOUT));
        template.set("newbieNextGift", statistics.getIntValue(PlayerStatistic.NEWBIE_GIFT));

        if (statistics.getIntValue(PlayerStatistic.NEWBIE_ROOM_LAYOUT) > 0 && statistics.getIntValue(PlayerStatistic.NEWBIE_GIFT) > 0) {
            int seconds = statistics.getIntValue(PlayerStatistic.NEWBIE_GIFT_TIME) - DateUtil.getCurrentTimeSeconds();

            if (BeginnerGiftManager.progress(playerDetails, statistics)) {
                seconds = statistics.getIntValue(PlayerStatistic.NEWBIE_GIFT_TIME) - DateUtil.getCurrentTimeSeconds();
                template.set("newbieNextGift", statistics.getIntValue(PlayerStatistic.NEWBIE_GIFT));
            }

            if (seconds < 0) {
                seconds = 0;
            }

            template.set("newbieGiftSeconds", seconds);
        }

        template.render();
    }

    public static void giftqueueHide(WebConnection connection) {
        if (!connection.session().getBoolean("authenticated")) {
            connection.send("");
            return;
        }

        int userId = connection.session().getInt("user.id");

        int nextGift = (int) PlayerStatisticsDao.getStatisticLong(userId, PlayerStatistic.NEWBIE_GIFT);

        if (nextGift == 3) {
            PlayerStatisticsDao.updateStatistic(userId, PlayerStatistic.NEWBIE_GIFT, 4);
        }

        connection.send("");
    }
}
