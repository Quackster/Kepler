package net.h4bbo.http.kepler.controllers.api;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.game.ads.AdManager;
import net.h4bbo.kepler.game.ads.Advertisement;

public class AdvertisementController {
    public static void getImg(WebConnection webConnection) {
        if (!webConnection.get().contains("ad")) {
            webConnection.send("");
            return;
        }

        Advertisement advertisement = AdManager.getInstance().getAd(webConnection.get().getInt("ad"));

        if (advertisement == null) {
            webConnection.send("");
            return;
        }

        webConnection.redirect(advertisement.getImage());
    }

    public static void getUrl(WebConnection webConnection) {
        if (!webConnection.get().contains("ad")) {
            webConnection.send("");
            return;
        }

        Advertisement advertisement = AdManager.getInstance().getAd(webConnection.get().getInt("ad"));

        if (advertisement == null) {
            webConnection.send("");
            return;
        }

        webConnection.redirect(advertisement.getUrl());
    }
}
