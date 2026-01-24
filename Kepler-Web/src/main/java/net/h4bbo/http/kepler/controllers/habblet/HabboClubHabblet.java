package net.h4bbo.http.kepler.controllers.habblet;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.dao.mysql.AlertsDao;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.dao.mysql.PlayerStatisticsDao;
import net.h4bbo.kepler.game.alerts.AlertType;
import net.h4bbo.kepler.game.club.ClubSubscription;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;
import net.h4bbo.kepler.server.rcon.messages.RconHeader;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.util.RconUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class HabboClubHabblet {
    public static void confirm(WebConnection webConnection) {
        int optionNumber = 1;

        try {
            optionNumber = Integer.parseInt(webConnection.post().getString("optionNumber"));
        } catch (Exception ex) {

        }

        if (optionNumber < 0 || optionNumber > 4) {
            return;
        }

        var choiceData = ClubSubscription.getChoiceData(optionNumber);

        var template = webConnection.template("habblet/habboClubConfirm");
        template.set("clubCredits", choiceData.getKey());
        template.set("clubDays", choiceData.getValue());
        template.set("clubType", optionNumber);
        template.render();
    }

    public static void subscribe(WebConnection webConnection) throws SQLException {
        if (!webConnection.session().getBoolean("authenticated")) {
            return;
        }

        int optionNumber = 1;

        try {
            optionNumber = Integer.parseInt(webConnection.post().getString("optionNumber"));
        } catch (Exception ex) {

        }

        var choiceData = ClubSubscription.getChoiceData(optionNumber);

        int credits = choiceData.getKey();
        int days = choiceData.getValue();

        var template = webConnection.template("habblet/habboClubSubscribe");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        if (playerDetails.getCredits() < credits) {
            template.set("subscribeMsg", "You don't have enough credits to complete the subscription purchase.");
        } else {
            template.set("subscribeMsg", "Congratulations! You have successfully subscribed to " + GameConfiguration.getInstance().getString("site.name") + " Club.");

            boolean firstTime = (playerDetails.getFirstClubSubscription() == 0);

            ClubSubscription.subscribeClub(playerDetails, optionNumber);
            PlayerStatisticsDao.updateStatistic(playerDetails.getId(), PlayerStatistic.CLUB_MEMBER_TIME_UPDATED, DateUtil.getCurrentTimeSeconds() + ClubSubscription.getClubGiftSeconds());

            if (playerDetails.isOnline()) {
                RconUtil.sendCommand(RconHeader.REFRESH_CLUB, new HashMap<>() {{
                    put("userId", playerDetails.getId());
                }});

                if (firstTime) {
                    RconUtil.sendCommand(RconHeader.REFRESH_HAND, new HashMap<>() {{
                        put("userId", playerDetails.getId());
                    }});
                }
            }
        }

        template.render();
    }

    public static void enddate(WebConnection webConnection) {
        var template = webConnection.template("habblet/habboClubEnddate");

        if (webConnection.session().getBoolean("authenticated")) {
            PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

            if (playerDetails.hasClubSubscription()) {
                template.set("hcDays", TimeUnit.SECONDS.toDays(playerDetails.getClubExpiration() - DateUtil.getCurrentTimeSeconds()));
            }
        }

        template.render();
    }

    public static void reminderRemove(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            return;
        }

        PlayerDetails playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));
        AlertsDao.disableAlerts(playerDetails.getId(), AlertType.HC_EXPIRED);

        webConnection.send("");
    }
}
