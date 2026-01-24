package net.h4bbo.http.kepler.controllers.site;

import io.netty.handler.codec.http.FullHttpResponse;
import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.dao.mysql.PlayerStatisticsDao;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;
import net.h4bbo.kepler.game.room.handlers.RoomSelectionHandler;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.server.Watchdog;
import net.h4bbo.http.kepler.util.SessionUtil;
import net.h4bbo.http.kepler.util.XSSUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ClientController {
    public static void client(WebConnection webConnection) throws SQLException {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/login_popup");
            return;
        }

        /*
        if (!VotingAPI.isVoted(webConnection)) {
            VotingAPI.redirectToClientVote(webConnection);
            return;
        }

         */
        int userId = webConnection.session().getInt("user.id");
        String getRequests = webConnection.request().uri().contains("?") ? "?" + webConnection.request().uri().split("\\?")[1] : "";

        /*
        ClientPreference clientPreference = SessionDao.getClientPreference(userId);

        if (clientPreference == ClientPreference.SHOCKWAVE) {
            webConnection.redirect("/shockwave_client" + getRequests);
        } else {
            webConnection.redirect("/flash_client" + getRequests);
        }*/

        webConnection.redirect("/shockwave_client" + getRequests);
    }

    public static void shockwaveclient(WebConnection webConnection) throws SQLException {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/login_popup");
            return;
        }

        webConnection.session().set("clientRequest", webConnection.request().uri());

        if (webConnection.session().getBoolean("clientAuthenticate")) {
            webConnection.redirect("/account/reauthenticate");
            return;
        }

        boolean forwardRoom = false;
        int forwardType = -1;
        int forwardId = -1;

        var template = webConnection.template("client");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        if (playerDetails == null) {
            SessionUtil.logout(webConnection);
            webConnection.redirect("/");
            return;
        }

        var pair = playerDetails.isBanned();

        if (pair != null) {
            webConnection.redirect("/account/banned");
            return;
        }

        if (webConnection.get().contains("createRoom") && StringUtils.isNumeric(webConnection.get().getString("createRoom"))) {
            int roomType = Integer.parseInt(webConnection.get().getString("createRoom"));
            boolean setGift = false;

            if (!playerDetails.canSelectRoom()) {
                int roomLayout = (int) PlayerStatisticsDao.getStatisticLong(playerDetails.getId(), PlayerStatistic.NEWBIE_ROOM_LAYOUT);

                if (roomLayout == 0) {
                    if (!(roomType < 0 || roomType > 5)) {
                        setGift = true;
                    }
                }
            } else {
                setGift = RoomSelectionHandler.selectRoom(playerDetails.getId(), roomType);
            }

            if (setGift) {
                PlayerStatisticsDao.updateStatistic(playerDetails.getId(), PlayerStatistic.NEWBIE_ROOM_LAYOUT, roomType + 1);
                PlayerStatisticsDao.updateStatistic(playerDetails.getId(), PlayerStatistic.NEWBIE_GIFT, 1);
                PlayerStatisticsDao.updateStatistic(playerDetails.getId(), PlayerStatistic.NEWBIE_GIFT_TIME, DateUtil.getCurrentTimeSeconds() + TimeUnit.DAYS.toSeconds(1));
            }

            playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));
            forwardRoom = true;

            forwardType = 2; // Private room
            forwardId = playerDetails.getSelectedRoomId();
        }

        if (webConnection.get().contains("forwardId")) {
            forwardRoom = true;
            try {
                forwardId = webConnection.get().getInt("roomId");
                forwardType = webConnection.get().getInt("forwardId");
            } catch (Exception ex) {

            }
        }

        if (webConnection.get().contains("shortcut")) {
            int redirectionId = 0;

            if (webConnection.get().getString("shortcut").equals("roomomatic")) {
                redirectionId = 1;
            }

            if (redirectionId > 0) {
                template.set("shortcut", "shortcut.id=" + redirectionId + ";");
            }
        }

        var ssoTicket = playerDetails.getSsoTicket();

        // Update sso ticket
        if (GameConfiguration.getInstance().getBoolean("reset.sso.after.login") || ssoTicket.isBlank()) {
            ssoTicket = UUID.randomUUID().toString();
            PlayerDao.setTicket(webConnection.session().getInt("user.id"), ssoTicket);
        }

        template.set("ssoTicket", ssoTicket);
        template.set("forwardRoom", forwardRoom);

        if (forwardRoom) {
            template.set("forward", "<param name=\"sw9\" value=\"forward.type=" + forwardType + ";forward.id=" + forwardId + ";processlog.url=\">");
            template.set("forwardSub", "sw9=\"forward.type=" + forwardType + ";forward.id=" + forwardId + ";processlog.url=\"");

            template.set("forwardScript", "<param name=\\\"sw9\\\" value=\\\"forward.type=" + forwardType + ";forward.id=" + forwardId + ";processlog.url=\\\">");
            template.set("forwardSubScript", "sw9=\\\"forward.type=" + forwardType + ";forward.id=" + forwardId + ";processlog.url=\\\"");
        }

        template.render();
    }

    public static void flashClient(WebConnection webConnection) throws SQLException {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/login_popup");
            return;
        }

        var template = webConnection.template("client_flash");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        webConnection.session().set("clientRequest", webConnection.request().uri());

        if (webConnection.session().getBoolean("clientAuthenticate")) {
            webConnection.redirect("/account/reauthenticate");
            return;
        }

        boolean forwardRoom = false;
        int forwardType = -1;
        int forwardId = -1;

        if (playerDetails == null) {
            SessionUtil.logout(webConnection);
            webConnection.redirect("/");
            return;
        }

        var pair = playerDetails.isBanned();

        if (pair != null) {
            webConnection.redirect("/account/banned");
            return;
        }

        if (webConnection.get().contains("createRoom") && StringUtils.isNumeric(webConnection.get().getString("createRoom"))) {
            int roomType = Integer.parseInt(webConnection.get().getString("createRoom"));
            boolean setGift = false;

            if (!playerDetails.canSelectRoom()) {
                int roomLayout = (int) PlayerStatisticsDao.getStatisticLong(playerDetails.getId(), PlayerStatistic.NEWBIE_ROOM_LAYOUT);

                if (roomLayout == 0) {
                    if (!(roomType < 0 || roomType > 5)) {
                        setGift = true;
                    }
                }
            } else {
                setGift = RoomSelectionHandler.selectRoom(playerDetails.getId(), roomType);
            }

            if (setGift) {
                PlayerStatisticsDao.updateStatistic(playerDetails.getId(), PlayerStatistic.NEWBIE_ROOM_LAYOUT, roomType + 1);
                PlayerStatisticsDao.updateStatistic(playerDetails.getId(), PlayerStatistic.NEWBIE_GIFT, 1);
                PlayerStatisticsDao.updateStatistic(playerDetails.getId(), PlayerStatistic.NEWBIE_GIFT_TIME, DateUtil.getCurrentTimeSeconds() + TimeUnit.DAYS.toSeconds(1));
            }

            playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));
            forwardRoom = true;

            forwardType = 2; // Private room
            forwardId = playerDetails.getSelectedRoomId();
        }

        if (webConnection.get().contains("forwardId")) {
            forwardRoom = true;

            try {
                forwardId = webConnection.get().getInt("roomId");
                forwardType = webConnection.get().getInt("forwardId");
            } catch (Exception ex) {

            }
        }

        // template.set("preferredCountry", PlayerStatisticsDao.getStatisticString(playerDetails.getId(), PlayerStatistic.HOTEL_VIEW).toLowerCase());
        template.set("forwardRoom", forwardRoom);
        template.set("forwardId", forwardId);
        template.set("forwardType", forwardType);

        // Update sso ticket
        var ssoTicket = playerDetails.getSsoTicket();

        // Update sso ticket
        if (GameConfiguration.getInstance().getBoolean("reset.sso.after.login") || ssoTicket == null || ssoTicket.isBlank()) {
            ssoTicket = UUID.randomUUID().toString();
            PlayerDao.setTicket(webConnection.session().getInt("user.id"), ssoTicket);
        }

        template.set("ssoTicket", ssoTicket);
        template.render();
    }

    public static void clientInstallShockwave(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/login_popup");
            return;
        }

        var template = webConnection.template("client_install_shockwave");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        var pair = playerDetails.isBanned();

        if (pair != null) {
            webConnection.redirect("/account/banned");
            return;
        }

        template.render();
    }

    public static void updateHabboCount(WebConnection webConnection) {
        FullHttpResponse httpResponse = ResponseBuilder.create("");
        httpResponse.headers().set("X-JSON", "{\"habboCountText\":\"" + NumberFormat.getNumberInstance(Locale.US).format(Watchdog.USERS_ONLNE) + " members online" + "\"}");
        webConnection.send(httpResponse);
    }

    public static void blank(WebConnection webConnection) {
        webConnection.send("");
    }

    public static void client_error(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        var template = webConnection.template("client_error");

        if (webConnection.session().getBoolean("authenticated")) {
            PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

            var pair = playerDetails.isBanned();

            if (pair != null) {
                webConnection.redirect("/account/banned");
                return;
            }
        }

        if (webConnection.get().contains("error_id")) {
            template.set("errorId", webConnection.get().getString("error_id"));
        }

        template.render();
    }

    public static void client_connection_failed(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        var template = webConnection.template("client_connection_failed");

        if (webConnection.session().getBoolean("authenticated")) {
            PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

            var pair = playerDetails.isBanned();

            if (pair != null) {
                webConnection.redirect("/account/banned");
                return;
            }
        }

        if (webConnection.get().contains("error_id")) {
            template.set("errorId", webConnection.get().getString("error_id"));
        }

        template.render();
    }

    public static void betaClient(WebConnection webConnection) throws SQLException {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/login_popup");
            return;
        }

        var template = webConnection.template("client_beta");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        webConnection.session().set("clientRequest", webConnection.request().uri());

        if (webConnection.session().getBoolean("clientAuthenticate")) {
            webConnection.redirect("/account/reauthenticate");
            return;
        }

        /*
        if (!VotingAPI.isVoted(webConnection)) {
            VotingAPI.redirectToClientVote(webConnection);
            return;
        }
        */

        boolean forwardRoom = false;
        int forwardType = -1;
        int forwardId = -1;

        if (playerDetails == null) {
            SessionUtil.logout(webConnection);
            webConnection.redirect("/");
            return;
        }

        var pair = playerDetails.isBanned();

        if (pair != null) {
            webConnection.redirect("/account/banned");
            return;
        }

        if (webConnection.get().contains("createRoom") && StringUtils.isNumeric(webConnection.get().getString("createRoom"))) {
            int roomType = Integer.parseInt(webConnection.get().getString("createRoom"));
            boolean setGift = false;

            if (!playerDetails.canSelectRoom()) {
                int roomLayout = (int) PlayerStatisticsDao.getStatisticLong(playerDetails.getId(), PlayerStatistic.NEWBIE_ROOM_LAYOUT);

                if (roomLayout == 0) {
                    if (!(roomType < 0 || roomType > 5)) {
                        setGift = true;
                    }
                }
            } else {
                setGift = RoomSelectionHandler.selectRoom(playerDetails.getId(), roomType);
            }

            if (setGift) {
                PlayerStatisticsDao.updateStatistic(playerDetails.getId(), PlayerStatistic.NEWBIE_ROOM_LAYOUT, roomType + 1);
                PlayerStatisticsDao.updateStatistic(playerDetails.getId(), PlayerStatistic.NEWBIE_GIFT, 1);
                PlayerStatisticsDao.updateStatistic(playerDetails.getId(), PlayerStatistic.NEWBIE_GIFT_TIME, DateUtil.getCurrentTimeSeconds() + TimeUnit.DAYS.toSeconds(1));
            }

            playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));
            forwardRoom = true;

            forwardType = 2; // Private room
            forwardId = playerDetails.getSelectedRoomId();
        }

        if (webConnection.get().contains("forwardId")) {
            forwardRoom = true;

            try {
                forwardId = webConnection.get().getInt("roomId");
                forwardType = webConnection.get().getInt("forwardId");
            } catch (Exception ex) {

            }
        }

        // template.set("preferredCountry", PlayerStatisticsDao.getStatisticString(playerDetails.getId(), PlayerStatistic.HOTEL_VIEW).toLowerCase());
        template.set("forwardRoom", forwardRoom);
        template.set("forwardId", forwardId);
        template.set("forwardType", forwardType);

        // Update sso ticket
        var ssoTicket = playerDetails.getSsoTicket();

        // Update sso ticket
        if (GameConfiguration.getInstance().getBoolean("reset.sso.after.login") || ssoTicket.isBlank()) {
            ssoTicket = UUID.randomUUID().toString();
            PlayerDao.setTicket(webConnection.session().getInt("user.id"), ssoTicket);
        }

        template.set("ssoTicket", ssoTicket);
        template.render();
    }
}
