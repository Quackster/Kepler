package net.h4bbo.http.kepler.controllers.habblet;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.*;
import net.h4bbo.kepler.game.groups.Group;
import net.h4bbo.kepler.server.rcon.messages.RconHeader;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.controllers.site.MinimailController;
import net.h4bbo.http.kepler.dao.CommunityDao;
import net.h4bbo.http.kepler.server.Watchdog;
import net.h4bbo.http.kepler.util.RconUtil;
import net.h4bbo.http.kepler.util.XSSUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.ToIntFunction;

public class ProxyHabblet {
    public static void moreInfo(WebConnection webConnection) {
        if (!webConnection.get().contains("hid")) {
            webConnection.send("");
            return;
        }

        if (webConnection.get().getString("hid").equals("h21")) {
            webConnection.send("\n" +
                    "<div id=\"staffpicks-rooms-habblet-list-container\" class=\"habblet-list-container groups-list\">\n" +
                    "    <ul class=\"habblet-list\">\n" +
                    "\n" +
                    "        <li class=\"even room-occupancy-2\" roomid=\"1\">\n" +
                    "            <div>\n" +
                    "                <span class=\"room-name\"><a href=\"http://localhost/client?forwardId=2&amp;roomId=1\" onclick=\"HabboClient.roomForward(this, '1', 'private'); return false;\" target=\"client\">Room name</a></span>\n" +
                    "                <span class=\"room-owner\"><a href=\"http://localhost/home/Alex\">Alex</a></span>                \n" +
                    "\t\t\t\t<p>test</p>\n" +
                    "            </div>\n" +
                    "        </li>\n" +
                    "    </ul>\n" +
                    "</div>\n" +
                    "\n");
            return;
        }

        if (webConnection.get().getString("hid").equals("h122")) {
            int limit = GameConfiguration.getInstance().getInteger("hot.groups.community.limit");

            var hotGroups = CommunityDao.getHotGroups(limit, 0);
            var hotSortedGroups = new ArrayList<>(hotGroups.keySet());
            hotSortedGroups.sort(Comparator.comparingInt((ToIntFunction<Group>) hotGroups::get).reversed());

            var hotHiddenGroups = CommunityDao.getHotGroups(limit, limit);
            var HotHiddenSortedGroups = new ArrayList<>(hotHiddenGroups.keySet());
            HotHiddenSortedGroups.sort(Comparator.comparingInt((ToIntFunction<Group>) hotHiddenGroups::get).reversed());


            Template template = webConnection.template("habblet/community_hot_groups");
            template.set("hotGroups", hotSortedGroups);
            template.set("hotHiddenGroups", HotHiddenSortedGroups);
            template.render();

            return;
        }

        if (webConnection.get().getString("hid").equals("h120")) {
            Template template = webConnection.template("habblet/showMoreRooms");
            template.set("highestRatedRooms", RoomDao.getHighestRatedRooms(5, 0));
            template.set("highestHiddenRatedRooms", RoomDao.getHighestRatedRooms(5, 5));
            template.render();
            return;
        }

        if (webConnection.get().getString("hid").equals("h24")) {
            Template template = webConnection.template("habblet/tagList");
            template.set("tagCloud", Watchdog.TAG_CLOUD_20);
            template.render();
            return;
        }

        if (webConnection.get().getString("hid").equals("groups")) {
            var hotGroups = CommunityDao.getHotGroups(GameConfiguration.getInstance().getInteger("hot.groups.limit"), 0);

            var sortedGroups = new ArrayList<>(hotGroups.keySet());
            sortedGroups.sort(Comparator.comparingInt((ToIntFunction<Group>) hotGroups::get).reversed());

            Template template = webConnection.template("habblet/hot_groups");
            template.set("groups", sortedGroups);
            template.render();
            return;
        }

        webConnection.send("");
    }

    public static void minimail(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        String habbletKey = "";

        if (webConnection.get().contains("habbletKey")) {
            habbletKey = webConnection.get().getString("habbletKey");
        }

        if (habbletKey.equalsIgnoreCase("news")) {
            webConnection.send("<div class=\"habblet-container \">\t\t\n" +
                    "\t\n" +
                    "\t<div id=\"news-habblet-container\">\n" +
                    "\t\n" +
                    "\t\t<div class=\"title\">\n" +
                    "\t\t\n" +
                    "\t\t\t<div class=\"habblet-close\"></div>\n" +
                    "\t\t\t\n" +
                    "\t\t\t<div>The shit you don't even wanna know!</div>\n" +
                    "\t\t\t\n" +
                    "\t\t</div>\n" +
                    "\t\t\n" +
                    "\t\t<div class=\"content-container\">\n" +
                    "\t\t\n" +
                    "\t\t\t<div id=\"news-articles\">\n" +
                    "\t\t\t\n" +
                    "\t\t\t\t<ul id=\"news-articlelist\" class=\"articlelist\" style=\"display: none\">\n" +
                    "\n" +
                    "\t\t\t\t</ul>\n" +
                    "\t\t\t\t\n" +
                    "\t\t\t</div>\n" +
                    "\t\t\t\n" +
                    "\t\t</div>\n" +
                    "\t\t\n" +
                    "\t\t<div class=\"news-footer\"></div>\n" +
                    "\t\n" +
                    "\t</div>\n" +
                    "\n" +
                    "\t<script type=\"text/javascript\">    \n" +
                    "\t\tL10N.put(\"news.promo.readmore\", \"Read more\").put(\"news.promo.close\", \"Close article\");\n" +
                    "\t\tNews.init(false);\n" +
                    "\t</script>\n" +
                    "\n" +
                    "</div>\n" +
                    "\n" +
                    "<!-- dependencies\n" +
                    "<link rel=\"stylesheet\" href=\"http://images.habbo.com/habboweb/%web_build%/web-gallery/v2/styles/news.css\" type=\"text/css\" />\n" +
                    "<script src=\"http://images.habbo.com/habboweb/%web_build%/web-gallery/static/js/news.js\" type=\"text/javascript\"></script>\n" +
                    "-->");
            return;
        }

        Template template = webConnection.template("habblet/minimail");
        webConnection.session().set("minimailLabel", "inbox");
        MinimailController.appendMessages(webConnection, template, true, false, false, false, false, false);
        template.set("minimailClient", true);
        template.render();
    }

    public static void clearhand(WebConnection connection) {
        if (!connection.session().getBoolean("authenticated")) {
            connection.send("");
            return;
        }

        /*
        System.out.println(connection.session().getString(XSSUtil.XSSKey));
        System.out.println(connection.session().getString(XSSUtil.XSSRequested));
        System.out.println(connection.session().getString(XSSUtil.XSSSeed));
        */

        if (!XSSUtil.verifyKey(connection, "/credits")) {
            connection.send("Failed to securely verify request");
            return;
        }

        int userId = connection.session().getInt("user.id");
        ItemDao.deleteHandItems(userId);

        RconUtil.sendCommand(RconHeader.REFRESH_HAND, new HashMap<>() {{
            put("userId", userId);
        }});

        connection.send("");
    }

    public static void token_generate(WebConnection connection) {
        if (!connection.session().getBoolean("authenticated")) {
            connection.send("");
            return;
        }

        String uuid = "token-" + UUID.randomUUID();
        connection.session().set("authenticationToken", uuid);
        connection.send(uuid);
    }
}
