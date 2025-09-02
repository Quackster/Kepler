package net.h4bbo.http.kepler.controllers.housekeeping;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.BanDao;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.http.kepler.Routes;
import net.h4bbo.http.kepler.game.housekeeping.HousekeepingManager;
import net.h4bbo.http.kepler.util.SessionUtil;

public class HousekeepingBansController {
    public static void bans(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        int currentPage = 0;

        if (client.get().contains("page")) {
            currentPage = Integer.parseInt(client.get().getString("page"));
        }

        String sortBy = "banned_at";

        if (client.get().contains("sort")) {
            if (client.get().getString("sort").equals("banned_at") ||
                    client.get().getString("sort").equals("banned_until")) {
                sortBy = client.get().getString("sort");
            }
        }

        Template tpl = client.template("housekeeping/users_bans");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "bans")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        tpl.set("pageName", "Bans");
        tpl.set("bans", BanDao.getActiveBans(currentPage, sortBy));
        tpl.set("nextBans", BanDao.getActiveBans(currentPage + 1, sortBy));
        tpl.set("previousBans", BanDao.getActiveBans(currentPage - 1, sortBy));
        tpl.set("page", currentPage);
        tpl.set("sortBy", sortBy);
        tpl.render();

        // Delete alert after it's been rendered
        client.session().delete("alertMessage");
    }
}
