package net.h4bbo.http.kepler.game.account;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.util.DateUtil;

public class CacheManager {
    public static void savePage(WebConnection connection, String pageName, String page, int maxLifetimeSeconds) {
        connection.session().set("savedCache" + pageName + "Time", String.valueOf(DateUtil.getCurrentTimeSeconds() + maxLifetimeSeconds));
        connection.session().set("savedCache" + pageName + "Source", page);
    }

    public static void deletePage(WebConnection connection, String pageName) {
        connection.session().delete("savedCache" + pageName + "Time");
        connection.session().delete("savedCache" + pageName + "Source");
    }

    public static String getPage(WebConnection connection, String pageName) {
        return connection.session().getString("savedCache" + pageName + "Source");
    }

    public static boolean useCachePage(WebConnection connection, String pageName) {
        if (connection.session().contains("savedCache" + pageName + "Time")) {
            try {
                long expire = connection.session().getLong("savedCache" + pageName + "Time");

                if (DateUtil.getCurrentTimeSeconds() < expire) {
                    return true;
                }
            } catch (Exception ex) {

            }
        }

        return false;
    }
}
