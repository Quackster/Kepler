package net.h4bbo.http.kepler.controllers.housekeeping;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.TransactionDao;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.http.kepler.Routes;
import net.h4bbo.http.kepler.game.housekeeping.HousekeepingManager;
import net.h4bbo.http.kepler.util.SessionUtil;
import org.apache.commons.lang3.StringUtils;

public class HousekeepingTransactionsController {

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

        Template tpl = client.template("housekeeping/transaction_lookup");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "transaction/lookup")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        try {
            if (client.post().getValues().size() > 0) {
                var transactions = TransactionDao.getTransactionsPastMonth(client.post().getString("searchQuery"), true);
                tpl.set("transactions", transactions);
            }

            if (client.get().getValues().size() > 0) {
                var transactions = TransactionDao.getTransactionsPastMonth(client.get().getString("searchQuery"), true);
                tpl.set("transactions", transactions);
            }
        } catch (Exception ex) {

        }

        tpl.set("pageName", "Transaction Lookup");
        tpl.render();

        // Delete alert after it's been rendered
        client.session().delete("alertMessage");
    }

    public static void item_lookup(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        Template tpl = client.template("housekeeping/transaction_item_lookup");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "transaction/lookup")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

            var transactions = TransactionDao.getTransactionByItem(StringUtils.isNumeric(client.get().getString("id")) ? client.get().getInt("id") : 0);
            tpl.set("transactions", transactions);

        tpl.set("pageName", "Transaction Lookup");
        tpl.render();

        // Delete alert after it's been rendered
        client.session().delete("alertMessage");
    }
}
