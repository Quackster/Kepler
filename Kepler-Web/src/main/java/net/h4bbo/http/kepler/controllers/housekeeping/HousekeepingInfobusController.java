package net.h4bbo.http.kepler.controllers.housekeeping;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.AdvertisementsDao;
import net.h4bbo.kepler.dao.mysql.InfobusDao;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.infobus.InfobusPoll;
import net.h4bbo.kepler.game.infobus.InfobusPollData;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.server.rcon.messages.RconHeader;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.http.kepler.Routes;
import net.h4bbo.http.kepler.dao.NewsDao;
import net.h4bbo.http.kepler.game.housekeeping.HousekeepingManager;
import net.h4bbo.http.kepler.game.news.NewsArticle;
import net.h4bbo.http.kepler.util.HtmlUtil;
import net.h4bbo.http.kepler.util.RconUtil;
import net.h4bbo.http.kepler.util.SessionUtil;
import net.h4bbo.http.kepler.util.piechart.PieChart;
import net.h4bbo.http.kepler.util.piechart.Slice;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class HousekeepingInfobusController {

    /**
     * Handle the /housekeeping/infobus_polls URI request
     *
     * @param client the connection
     */
    public static void polls(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        Template tpl = client.template("housekeeping/infobus_polls");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "infobus")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        tpl.set("pageName", "View Infobus Polls");
        tpl.set("infobusPolls", InfobusDao.getInfobusPolls());
        tpl.render();

        // Delete alert after it's been rendered
        client.session().delete("alertMessage");
    }

    public static void create_polls(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        Template tpl = client.template("housekeeping/infobus_polls_create");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "infobus")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        try {
            if (client.post().getValues().size() > 0) {
                String question = client.post().getString("question");

                client.session().set("alertColour", "success");
                client.session().set("alertMessage", "Infobus poll has been created successfully");

                InfobusPollData infobusPollData = new InfobusPollData(question);
                infobusPollData.getAnswers().addAll(client.post().getArray("answers[]"));
                InfobusDao.createInfobusPoll(playerDetails.getId(), infobusPollData);

                client.redirect("/" + Routes.HOUSEKEEPING_PATH + "/infobus_polls");
                return;
            }
        } catch (Exception ex) {

        }

        tpl.set("pageName", "Create Infobus Poll");
        tpl.set("oneHourLater", DateUtil.getDate(DateUtil.getCurrentTimeSeconds() + TimeUnit.HOURS.toSeconds(1), "yyyy-MM-dd'T'HH:mm"));
        tpl.render();

        // Delete alert after it's been rendered
        client.session().delete("alertMessage");
    }

    /**
     * Handle the /housekeeping/infobus_polls/delete URI request
     *
     * @param client the connection
     */
    public static void delete(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        Template tpl = client.template("housekeeping/articles");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");
        InfobusPoll poll = InfobusDao.get(client.get().getInt("id"));

        if (poll == null) {
            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "The infobus poll does not exist");
            client.redirect("/" + Routes.HOUSEKEEPING_PATH + "/infobus_polls");
            return;
        }

        if (poll.getInitiatedBy() != playerDetails.getId()) {
            if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "infobus/delete_any")) {
                client.session().set("alertColour", "danger");
                client.session().set("alertMessage", "No permission to delete other polls");
                client.redirect("/" + Routes.HOUSEKEEPING_PATH);
                return;
            }
        }

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "infobus/delete_own")) {
            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "No permission to delete");
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        if (!client.get().contains("id")) {
            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "There was no infobus poll selected to delete");
        } else {
            var answers = InfobusDao.getAnswers(poll.getId());
            int totalAnswers = answers.values().stream().mapToInt(Integer::intValue).sum();

            if (totalAnswers > 0) {
                client.session().set("alertColour", "danger");
                client.session().set("alertMessage", "You can't delete a poll with answers");
                client.redirect("/" + Routes.HOUSEKEEPING_PATH + "/infobus_polls");
                return;
            }

            client.session().set("alertColour", "success");
            client.session().set("alertMessage", "Successfully deleted the infobus poll");

            InfobusDao.delete(client.get().getInt("id"));
            InfobusDao.clearAnswers(client.get().getInt("id"));
        }

        client.redirect("/" + Routes.HOUSEKEEPING_PATH + "/infobus_polls");

    }

    /**
     * Handle the /housekeeping/infobus_polls/send_poll URI request
     *
     * @param client the connection
     */
    public static void send_poll(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        Template tpl = client.template("housekeeping/articles");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "infobus")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        InfobusPoll poll = InfobusDao.get(client.get().getInt("id"));

        if (poll == null) {
            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "The infobus poll does not exist");
            client.redirect("/" + Routes.HOUSEKEEPING_PATH + "/infobus_polls");
            return;
        }

        client.session().set("alertColour", "warning");
        client.session().set("alertMessage", "The infobus poll request has been sent");

        RconUtil.sendCommand(RconHeader.INFOBUS_POLL, new HashMap<>() {{
            put("pollId", String.valueOf(poll.getId()));
        }});

        client.redirect("/" + Routes.HOUSEKEEPING_PATH + "/infobus_polls");

    }

    /**
     * Handle the /housekeeping/infobus_polls/edit URI request
     *
     * @param client the connection
     */
    public static void edit(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        Template tpl = client.template("housekeeping/infobus_polls_edit");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "infobus")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        InfobusPoll infobusPoll = InfobusDao.get(client.get().getInt("id"));

        if (!client.get().contains("id")) {
            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "There was no infobus poll selected to edit");
            client.redirect("/" + Routes.HOUSEKEEPING_PATH + "/infobus_polls");
            return;
        } else if (infobusPoll == null) {
            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "The infobus poll does not exist");
            client.redirect("/" + Routes.HOUSEKEEPING_PATH + "/infobus_polls");
            return;
        } else {
            if (client.post().queries().size() > 0) {
                int id = infobusPoll.getId();
                String question = client.post().getString("question");

                var answers = InfobusDao.getAnswers(infobusPoll.getId());
                int totalAnswers = answers.values().stream().mapToInt(Integer::intValue).sum();

                if (totalAnswers > 0) {
                    client.session().set("alertColour", "danger");
                    client.session().set("alertMessage", "You can't edit the poll if it has answers");
                } else {
                    /*var activePoll = InfobusDao.getActivePoll();

                    if (activePoll != null && activePoll.getId() != infobusPoll.getId() && enabled) {
                        client.session().set("alertColour", "warning");
                        client.session().set("alertMessage", "Cannot activate this poll while there's already a different active poll");
                        enabled = false;
                    } else {*/
                        client.session().set("alertColour", "success");
                        client.session().set("alertMessage", "The infobus poll was successfully saved");

                    InfobusPollData infobusPollData = new InfobusPollData(question);
                    infobusPollData.getAnswers().addAll(client.post().getArray("answers[]"));
                    InfobusDao.saveInfobusPoll(id, infobusPollData);
                }

                //RconUtil.sendCommand(RconHeader.REFRESH_INFOBUS_POLLS, new HashMap<>());
                client.redirect("/" + Routes.HOUSEKEEPING_PATH + "/infobus_polls");
                return;
            }

            //tpl.set("pollDate", DateUtil.getDate(infobusPoll.getExpiresAt(), "yyyy-MM-dd'T'HH:mm"));
            tpl.set("poll", infobusPoll);
        }

        tpl.render();

        // Delete alert after it's been rendered
        client.session().delete("alertMessage");
    }

    /**
     * Handle the /housekeeping/infobus_polls/edit URI request
     *
     * @param client the connection
     */
    public static void view_results(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        Template tpl = client.template("housekeeping/infobus_polls_view");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "infobus")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        InfobusPoll infobusPoll = InfobusDao.get(client.get().getInt("id"));

        if (!client.get().contains("id")) {
            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "There was no infobus poll selected to edit");
            client.redirect("/" + Routes.HOUSEKEEPING_PATH + "/infobus_polls");
            return;
        } else if (infobusPoll == null) {
            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "The infobus poll does not exist");
            client.redirect("/" + Routes.HOUSEKEEPING_PATH + "/infobus_polls");
            return;
        } else {
            tpl.set("poll", infobusPoll);
        }

        var image = new BufferedImage(500, 250, BufferedImage.TYPE_INT_ARGB);

        var answers = InfobusDao.getAnswers(infobusPoll.getId());
        int totalAnswers = answers.values().stream().mapToInt(Integer::intValue).sum();

        var slices = new ArrayList<Slice>();

        int i = 0;

        if (totalAnswers > 0) {
            for (var answer : answers.entrySet()) {
                Color color = null;

                if (i == 0) {
                    color = Color.BLUE;
                }

                if (i == 1) {
                    color = Color.RED;
                }

                if (i == 2) {
                    color = Color.YELLOW;
                }

                if (i == 3) {
                    color = Color.PINK;
                }

                if (i == 4) {
                    color = Color.ORANGE;
                }

                try {
                    slices.add(new Slice(infobusPoll.getPollData().getAnswers().get(answer.getKey()), (double) (answer.getValue() > 0 ? totalAnswers / answer.getValue() : 0), color));
                } catch (Exception ex) {
                    client.session().set("alertColour", "danger");
                    client.session().set("alertMessage", "There was an answer to a question that doesn't exist, some answers may not be visible on this chart");
                }

                i++;
            }
        }

        new PieChart(image, slices);

        tpl.set("imageData", "data:image/png;base64," + HtmlUtil.encodeToString(image, "PNG"));
        tpl.set("noAnswers", totalAnswers == 0);

        tpl.render();

        // Delete alert after it's been rendered
        client.session().delete("alertMessage");
    }

    public static void clear_results(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        Template tpl = client.template("housekeeping/infobus_polls_view");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "infobus")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        InfobusPoll infobusPoll = InfobusDao.get(client.get().getInt("id"));

        if (!client.get().contains("id")) {
            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "There was no infobus poll selected to edit");
            client.redirect("/" + Routes.HOUSEKEEPING_PATH + "/infobus_polls");
            return;
        } else if (infobusPoll == null) {
            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "The infobus poll does not exist");
            client.redirect("/" + Routes.HOUSEKEEPING_PATH + "/infobus_polls");
            return;
        } else {
            client.session().set("alertColour", "success");
            client.session().set("alertMessage", "The infobus poll has had all answers cleared");

            InfobusDao.clearAnswers(infobusPoll.getId());
            client.redirect("/" + Routes.HOUSEKEEPING_PATH + "/infobus_polls");
        }
    }

    public static void close_event(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        int userId = client.session().getInt("user.id");
        PlayerDetails playerDetails = PlayerDao.getDetails(userId);

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "infobus")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        client.session().set("alertColour", "success");
        client.session().set("alertMessage", "The infobus status has been sent");

        RconUtil.sendCommand(RconHeader.INFOBUS_END_EVENT, new HashMap<>());
        client.redirect("/" + Routes.HOUSEKEEPING_PATH + "/infobus_polls");
    }

    public static void door_status(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        int userId = client.session().getInt("user.id");
        PlayerDetails playerDetails = PlayerDao.getDetails(userId);

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "infobus")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        RconUtil.sendCommand(RconHeader.INFOBUS_DOOR_STATUS, new HashMap<>() {{
            put("doorStatus", String.valueOf(client.get().getInt("status")));
        }});

        client.session().set("alertColour", "success");
        client.session().set("alertMessage", "The infobus door status has been sent");

        client.redirect("/" + Routes.HOUSEKEEPING_PATH + "/infobus_polls");
    }
}
