package net.h4bbo.http.kepler.controllers.housekeeping;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.http.kepler.Routes;
import net.h4bbo.http.kepler.dao.HousekeepingDao;
import net.h4bbo.http.kepler.dao.housekeeping.HousekeepingPlayerDao;
import net.h4bbo.http.kepler.game.housekeeping.HousekeepingManager;
import net.h4bbo.http.kepler.game.housekeeping.HousekeepingStats;
import net.h4bbo.http.kepler.util.SessionUtil;

public class HousekeepingController {

    /**
     * Handle the /housekeeping URI request
     *
     * @param client the connection
     */
    public static void dashboard(WebConnection client) {
        // If they are logged in, send them to the /me page
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            Template tpl = client.template("housekeeping/login");
            tpl.render();
        } else {

            int currentPage = 0;

            if (client.get().contains("page")) {
                currentPage = Integer.parseInt(client.get().getString("page"));
            }

            boolean zeroCoinsFlag = false;

            if (client.get().contains("zerocoins")) {
                zeroCoinsFlag = true;
            }

            String sortBy = "created_at";

            if (client.get().contains("sort")) {
                if (client.get().getString("sort").equals("last_online") ||
                    client.get().getString("sort").equals("created_at")) {
                    sortBy = client.get().getString("sort");
                }
            }

            Template tpl = client.template("housekeeping/dashboard");
            tpl.set("housekeepingManager", HousekeepingManager.getInstance());

            tpl.set("pageName", "Dashboard");
            tpl.set("players", HousekeepingPlayerDao.getPlayers(currentPage, zeroCoinsFlag, sortBy));
            tpl.set("nextPlayers", HousekeepingPlayerDao.getPlayers(currentPage + 1, zeroCoinsFlag, sortBy));
            tpl.set("previousPlayers", HousekeepingPlayerDao.getPlayers(currentPage - 1, zeroCoinsFlag, sortBy));
            tpl.set("page", currentPage);
            tpl.set("sortBy", sortBy);
            tpl.set("stats", new HousekeepingStats(
                    HousekeepingDao.getUserCount(),
                    HousekeepingDao.getInventoryItemsCount(),
                    HousekeepingDao.getRoomItemCount(),
                    HousekeepingDao.getGroupCount(),
                    HousekeepingDao.getPetCount(),
                    HousekeepingDao.getPhotoCount()));
            tpl.set("zeroCoinsFlag", zeroCoinsFlag);
            tpl.render();

            // Delete alert after it's been rendered
            client.session().delete("alertMessage");
        }
    }

    /**
     * Handle the /housekeeping/login URI request
     *
     * @param client the connection
     */
    public static void login(WebConnection client) {
        String[] fieldCheck = new String[] { "hkusername", "hkpassword" };

        for (String field : fieldCheck) {

            if (client.post().contains(field) &&
                client.post().getString(field).length() > 0) {
                continue;
            }

            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "You need to enter both your email and password");
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        /*if (!PlayerDao.emailExists(client.post().get("hkemail"), 0)) {
            client.session().set("showAlert"434, true);
            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "You have entered invalid details");
            client.redirect("/housekeeping");
            return;
        }*/

        PlayerDetails playerDetails = new PlayerDetails();

        if (!PlayerDao.login(playerDetails, client.post().getString("hkusername"), client.post().getString("hkpassword"))) {
            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "You have entered invalid details");
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "root/login")) {
            client.session().set("alertColour", "warning");
            client.session().set("alertMessage", "You don't have permission");
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        client.session().set(SessionUtil.LOGGED_IN_HOUSKEEPING, true);
        client.session().set(SessionUtil.USER_ID, String.valueOf(playerDetails.getId()));
        client.redirect("/" + Routes.HOUSEKEEPING_PATH);
    }

    /**
     * Handle the /housekeeping/login URI request
     *
     * @param client the connection
     */
    public static void logout(WebConnection client) {
        if (client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.session().set("alertColour", "success");
            client.session().set("alertMessage", "Successfully logged out!");
            client.session().set(SessionUtil.LOGGED_IN_HOUSKEEPING, false);
        }

        client.redirect("/" + Routes.HOUSEKEEPING_PATH);
    }
}