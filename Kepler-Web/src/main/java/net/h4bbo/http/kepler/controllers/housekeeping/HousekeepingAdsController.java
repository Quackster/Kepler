package net.h4bbo.http.kepler.controllers.housekeeping;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.AdvertisementsDao;
import net.h4bbo.kepler.game.ads.AdManager;
import net.h4bbo.kepler.game.ads.Advertisement;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.server.rcon.messages.RconHeader;
import net.h4bbo.http.kepler.Routes;
import net.h4bbo.http.kepler.game.housekeeping.HousekeepingManager;
import net.h4bbo.http.kepler.util.RconUtil;
import net.h4bbo.http.kepler.util.SessionUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class HousekeepingAdsController {

    /**
     * Handle the /housekeeping/room_ads URI request
     *
     * @param client the connection
     */
    public static void roomads(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        Template tpl = client.template("housekeeping/room_ads");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "room_ads")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        try {
            if (client.post().queries().size() > 0) {
                List<Advertisement> advertisementList = new ArrayList<>();

                for (var kvp : client.post().getValues().entrySet()) {
                    String key = kvp.getKey();
                    String value = kvp.getValue();

                    if (!key.startsWith("roomad-id-")) {
                        continue;
                    }

                    int roomId = client.post().getInt("roomad-" + value + "-roomid");
                    boolean isLoadingAd = client.post().contains("roomad-" + value + "-loading-ad") && client.post().getString("roomad-" + value + "-loading-ad").equalsIgnoreCase("on");
                    boolean isEnabled = client.post().contains("roomad-" + value + "-enabled") && client.post().getString("roomad-" + value + "-enabled").equalsIgnoreCase("on");
                    String image = client.post().getString("roomad-" + value + "-image");
                    String url = client.post().getString("roomad-" + value + "-url");

                    advertisementList.add(new Advertisement(Integer.parseInt(value), isLoadingAd, roomId, image, url, isEnabled));
                }

                AdvertisementsDao.updateAds(advertisementList);
                AdManager.getInstance().reset();

                /*
                for (var kvp : client.post().getValues().entrySet()) {
                    String key = kvp.getKey();
                    String value = kvp.getValue();

                    System.out.println(key + " / " + value);
                }*/

                client.session().set("alertColour", "success");
                client.session().set("alertMessage", "All room ads have been saved successfully!");

                RconUtil.sendCommand(RconHeader.REFRESH_ADS, new HashMap<>());
            }

        } catch (Exception ex) {

        }

        var advertisements = new ArrayList<>(AdManager.getInstance().getAds());//.stream().sorted();
        advertisements.sort(Comparator.comparingInt(Advertisement::getId));

        tpl.set("pageName", "Room Ads");
        tpl.set("roomAds", advertisements);
        tpl.render();

        // Delete alert after it's been rendered
        client.session().delete("alertMessage");
    }

    public static void delete(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        Template tpl = client.template("housekeeping/room_ads");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "room_ads")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        try {
            int id = client.get().getInt("id");
            AdvertisementsDao.deleteAd(id);

            client.session().set("alertColour", "danger");
            client.session().set("alertMessage", "Room ad has been deleted successfully");

            RconUtil.sendCommand(RconHeader.REFRESH_ADS, new HashMap<>());
        } catch (Exception ex) {

        }

        AdManager.getInstance().reset();
        var advertisements = AdManager.getInstance().getAds();//.stream().sorted();


        tpl.set("pageName", "Room Ads");
        tpl.set("roomAds", advertisements);
        tpl.render();

        // Delete alert after it's been rendered
        client.session().delete("alertMessage");
    }

    public static void create(WebConnection client) {
        if (!client.session().getBoolean(SessionUtil.LOGGED_IN_HOUSKEEPING)) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        Template tpl = client.template("housekeeping/room_ads_create");
        tpl.set("housekeepingManager", HousekeepingManager.getInstance());

        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (!HousekeepingManager.getInstance().hasPermission(playerDetails.getRank(), "room_ads")) {
            client.redirect("/" + Routes.HOUSEKEEPING_PATH);
            return;
        }

        try {
            if (client.post().getValues().size() > 0) {
                int roomId = client.post().getInt("roomid");
                String url = client.post().getString("url");
                String image = client.post().getString("image");
                boolean isEnabled = client.post().contains("enabled") && client.post().getString("enabled").equalsIgnoreCase("on");
                boolean isRoomLoadingAd = client.post().contains("loading-ad") && client.post().getString("loading-ad").equalsIgnoreCase("on");

                AdvertisementsDao.create(roomId, url, image, isEnabled, isRoomLoadingAd);
                AdManager.getInstance().reset();

                client.session().set("alertColour", "success");
                client.session().set("alertMessage", "Room ad has been created successfully");

                RconUtil.sendCommand(RconHeader.REFRESH_ADS, new HashMap<>());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        tpl.set("pageName", "Room Ads");
        tpl.render();

        // Delete alert after it's been rendered
        client.session().delete("alertMessage");
    }
}
