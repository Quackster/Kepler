package net.h4bbo.http.kepler.controllers.site;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.dao.mysql.GroupDao;
import net.h4bbo.kepler.dao.mysql.MessengerDao;
import net.h4bbo.kepler.dao.mysql.RoomDao;
import net.h4bbo.kepler.game.messenger.MessengerUser;
import net.h4bbo.http.kepler.util.XSSUtil;

import java.util.Comparator;
import java.util.stream.Collectors;

public class QuickmenuController {
    public static void groups(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        var groups = GroupDao.getJoinedGroups(webConnection.session().getInt("user.id"));

        var tpl = webConnection.template("quickmenu/groups");
        tpl.set("groups", groups);
        tpl.render();
    }

    public static void friends(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        var friends =  MessengerDao.getFriends(webConnection.session().getInt("user.id"));

        var friendsOnline = friends.values().stream()
                .filter(MessengerUser::isOnline)
                .sorted(Comparator.comparingLong(MessengerUser::getLastOnline).reversed())
                .limit(10)
                .collect(Collectors.toList());

        var friendsOffline = friends.values().stream()
                .filter(user -> !user.isOnline())
                .sorted(Comparator.comparingLong(MessengerUser::getLastOnline).reversed())
                .limit(10)
                .collect(Collectors.toList());

        var tpl = webConnection.template("quickmenu/friends_all");
        tpl.set("onlineFriends", friendsOnline);
        tpl.set("offlineFriends", friendsOffline);
        tpl.render();
    }

    public static void rooms(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        var tpl = webConnection.template("quickmenu/rooms");
        tpl.set("rooms", RoomDao.getRoomsByUserId(webConnection.session().getInt("user.id")));
        tpl.render();
    }
}
