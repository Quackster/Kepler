package net.h4bbo.http.kepler.controllers.api;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.http.kepler.dao.VerifyDao;

public class VerifyController {
    public static void get(WebConnection webConnection) {
        if (webConnection.getMatches().isEmpty() || webConnection.getMatches().get(0).isBlank()) {
            webConnection.send("error: INVALID");
            return;
        }

        var username = VerifyDao.getName(webConnection.getMatches().get(0));

        if (username == null) {
            webConnection.send("error: INVALID");
            return;
        }

        webConnection.send(username);
    }

    public static void clear(WebConnection webConnection) {
        if (webConnection.getMatches().isEmpty() || webConnection.getMatches().get(0).isBlank()) {
            webConnection.send("error: INVALID");
            return;
        }

        try {
            VerifyDao.clearName(webConnection.getMatches().get(0));
            webConnection.send("SUCCESS");
        } catch (Exception ex) {
            webConnection.send("error: INVALID");
        }
    }
}