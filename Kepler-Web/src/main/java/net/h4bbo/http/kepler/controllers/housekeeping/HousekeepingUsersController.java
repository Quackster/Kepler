package net.h4bbo.http.kepler.controllers.housekeeping;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.server.netty.NettyPlayerNetwork;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.http.kepler.Routes;
import net.h4bbo.http.kepler.dao.housekeeping.HousekeepingPlayerDao;
import net.h4bbo.http.kepler.game.housekeeping.HousekeepingManager;
import net.h4bbo.http.kepler.util.SessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.ArrayList;
import java.util.List;

public class HousekeepingUsersController {

    /**
     * Handle the /housekeeping/users/imitate/ URI request
     *
     * @param webConnection the connection
     */
    public static void imitate(WebConnection webConnection) {
        if (!webConnection.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            webConnection.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        Template tpl = webConnection.template("housekeeping/users_create");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "user/imitate")) {
            webConnection.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        var playerName = webConnection.getMatches().get(0);
        PlayerDetails player = PlayerDao.getDetails(playerName);

        if (playerName == null)
            return;

        webConnection.session().set("authenticated", true);
        webConnection.session().set("captcha.invalid", false);
        webConnection.session().set("user.id", player.getId());
        webConnection.session().set("clientAuthenticate", false);
        webConnection.session().set(SessionUtil.LOGGED_IN_HOUSKEEPING, false);
        webConnection.session().set("lastRequest", String.valueOf(DateUtil.getCurrentTimeSeconds() + SessionUtil.REAUTHENTICATE_TIME));
        webConnection.redirect("/me");

    }

    /**
     * Handle the /housekeeping/users/search URI request
     *
     * @param client the connection
     */
    public static void search(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        Template tpl = client.template("housekeeping/users_search");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "user/search")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        if (client.post().queries().size() > 0) {
            String[] fieldCheck = new String[]{"searchField", "searchQuery", "searchType" };

            for (String field : fieldCheck) {
                if (client.post().contains(field) && client.post().getString(field).length() > 0) {
                    continue;
                }

                client.session().set("alertColour", "danger");
                client.session().set("alertMessage", "You need to enter all fields");
                tpl.render();

                // Delete alert after it's been rendered
                client.session().delete("alertMessage");
                return;
            }

            String field = client.post().getString("searchField");
            String input = client.post().getString("searchQuery");
            String type = client.post().getString("searchType");

            List<String> whitelistColumns = new ArrayList<>();
            whitelistColumns.add("username");
            whitelistColumns.add("id");
            whitelistColumns.add("credits");
            whitelistColumns.add("pixels");
            whitelistColumns.add("mission");

            List<PlayerDetails> players = null;

            if (whitelistColumns.contains(field)) {
                players = HousekeepingPlayerDao.search(type, field, input);
            } else {
                players = new ArrayList<>();
            }

            tpl.set("players", players);
        }

        tpl.set("pageName", "Search Users");
        tpl.render();

        // Delete alert after it's been rendered
        client.session().delete("alertMessage");
    }

    /**
     * Handle the /housekeeping/users/create URI request
     *
     * @param client the connection
     */
    public static void create(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        Template tpl = client.template("housekeeping/users_create");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "user/create")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }
        /*tpl.set("defaultFigure", Configuration.REGISTER_FIGURE);
        tpl.set("defaultMission", Configuration.REGISTER_MOTTO);
        tpl.set("defaultCredits", Configuration.REGISTER_CREDITS);
        tpl.set("defaultDuckets", Configuration.REGISTER_DUCKETS);*/

        if (client.post().queries().size() > 0) {
            String[] fieldCheck = new String[]{"username", "password", "confirmpassword", "figure", "email", "mission"};

            for (String field : fieldCheck) {
                if (client.post().contains(field) && client.post().getString(field).length() > 0) {
                    continue;
                }

                client.session().set("alertColour", "danger");
                client.session().set("alertMessage", "You need to enter all fields");
            }

            if (!client.session().contains("alertMessage")) {
                client.session().set("alertColour", "warning");

                /*if (PlayerDao.emailExists(client.post().get("email"), 0)) {
                    client.session().set("alertMessage", "The email chosen is already in use");

                } else */
                if (!client.post().getString("password").equals(client.post().getString("confirmpassword"))) {
                    client.session().set("alertMessage", "The two passwords do not match");
                } else if (client.post().getString("password").length() < 6) {
                    client.session().set("alertMessage", "The password needs to be at least 6 or more characters");
                } else if (!EmailValidator.getInstance().isValid(client.post().getString("email"))) {
                    client.session().set("alertMessage", "The email entered is not valid");

                }
            }

            // Successful maybe?
            if (client.post().queries().size() > 0 && !client.session().contains("alertMessage")) {
                int userId = -1;
                //int userId = PlayerDao.create(client.post().get("username"), client.post().get("email"), client.post().get("password"), client.post().get("mission"), client.post().get("figure"));

                client.session().set("alertColour", "success");
                client.session().set("alertMessage", "The new user has been successfully created. <a href=\"/houskeeping/users/edit?id=" + userId + "\">Click here</a> to edit them.");
            }
        }

        tpl.set("pageName", "Create User");
        tpl.render();

        // Delete alert after it's been rendered
        client.session().delete("alertMessage");
    }

    /**
     * Handle the /housekeeping/users/edit URI request
     *
     * @param client the connection
     */
    public static void edit(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        Template tpl = client.template("housekeeping/users_edit");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "user/edit")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        if (!client.get().contains("id")) {
            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "You did not select a user to edit");
        }

        if (client.post().queries().size() > 0) {
            String[] fieldCheck = new String[]{"username", "figure", "email", "motto", "credits", "pixels"};

            for (String field : fieldCheck) {
                if (client.post().contains(field) && client.post().getString(field).length() > 0) {
                    continue;
                }

                client.session().set("alertColour", "danger");
                client.session().set("alertMessage", "You need to enter all fields. The " + field + " field is missing.");
            }

            if (!client.session().contains("alertMessage")) {
                client.session().set("alertColour", "warning");

                /*if (PlayerDao.emailExists(client.post().get("email"), client.get().getInt("id"))) {
                    client.session().set("alertMessage", "The email chosen is already in use");
                } else */

                if (!EmailValidator.getInstance().isValid(client.post().getString("email"))) {
                    client.session().set("alertMessage", "The email entered is not valid");
                } else if (!StringUtils.isNumeric(client.post().getString("credits"))) {
                    client.session().set("alertMessage", "The value supplied for credits is not a number");
                } else if (!StringUtils.isNumeric(client.post().getString("pixels"))) {
                    client.session().set("alertMessage", "The value supplied for pixels is not a number");
                }
            }
        }

        PlayerDetails player = PlayerDao.getDetails(client.get().getInt("id"));

        if (player == null) {
            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "The user does not exist");
        } else {
            //Player session = client.session().get(SessionUtil.PLAYER, Player.class);
            PlayerDetails session = PlayerDao.getDetails(client.session().getInt("user.id"));

            if (session.getRank().getRankId() <= player.getRank().getRankId()) {
                client.session().set("alertColour", "danger");
                client.session().set("alertMessage", "You cannot edit someone that has a equal or higher rank than you");
            } else {
                if (client.post().queries().size() > 0 && !client.session().contains("alertMessages")) {
                    player.setFigure(client.post().getString("figure"));
                    player.setMotto(client.post().getString("motto"));
                    player.setCredits(Integer.parseInt(client.post().getString("credits")));
                    player.setEmail(client.post().getString("email"));

                    PlayerDao.saveDetails(player.getId(), player.getFigure(), player.getPoolFigure(), player.getSex());
                    PlayerDao.saveMotto(player.getId(), player.getMotto());
                    PlayerDao.saveCurrency(player.getId(), player.getCredits());
                    PlayerDao.saveEmail(player.getId(), player.getEmail());

                    client.session().set("alertColour", "success");
                    client.session().set("alertMessage", "The user has been successfully saved");
                }
            }

            tpl.set("playerId", player.getId());
            tpl.set("playerUsername", player.getName());
            tpl.set("playerEmail", player.getEmail());
            tpl.set("playerMotto", player.getMotto());
            tpl.set("playerPixels", 0);
            tpl.set("playerCredits", player.getCredits());
            tpl.set("playerFigure", player.getFigure());
        }

        tpl.set("pageName", "Edit User");
        tpl.render();

        // Delete alert after it's been rendered
        client.session().delete("alertMessage");
    }
}
