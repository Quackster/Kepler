package net.h4bbo.http.kepler.controllers.housekeeping;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.SettingsDao;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.Routes;
import net.h4bbo.http.kepler.game.housekeeping.HousekeepingManager;
import net.h4bbo.http.kepler.util.ConfigEntry;
import net.h4bbo.http.kepler.util.SessionUtil;
import net.h4bbo.http.kepler.util.config.WebSettingsConfigWriter;

import java.util.ArrayList;
import java.util.Comparator;

public class HousekeepingConfigController {
    /**
     * Handle the /housekeeping/articles URI request
     *
     * @param client the connection
     */
    public static void configurations(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        Template tpl = client.template("housekeeping/configurations");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "configuration")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        if (client.post().queries().size() > 0) {
            SettingsDao.updateSettings(client.post().getValues().entrySet());

            // Reload config
            // GameConfiguration.getInstance(new WebSettingsConfigWriter());

            client.session().set("alertColour", "success");
            client.session().set("alertMessage", "All configuration values have been saved successfully! It will take effect within 30 seconds.");
        }

        var settings = new ArrayList<ConfigEntry>();

        for (var setting : SettingsDao.getAllSettings().entrySet()) {
            settings.add(new ConfigEntry(setting.getKey(), setting.getValue()));
        }

        settings.sort(Comparator.comparing(ConfigEntry::getKey));
        //var settings = SettingsDao.getAllSettings().entrySet();//.stream().sorted();

        tpl.set("pageName", "Configurations");
        tpl.set("configs", settings);
        tpl.render();

        // Delete alert after it's been rendered
        client.session().delete("alertMessage");
    }
}
