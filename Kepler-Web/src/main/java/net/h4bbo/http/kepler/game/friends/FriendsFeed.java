package net.h4bbo.http.kepler.game.friends;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.game.messenger.MessengerUser;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.dao.mysql.AlertsDao;

import java.util.stream.Collectors;

public class FriendsFeed {
    public static void createFriendsOnline(WebConnection webConnection, Template template) {
        if (!webConnection.session().getBoolean("authenticated")) {
            return;
        }

        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        if (playerDetails == null) {
            return;
        }

        var friends = AlertsDao.getOnlineFriends(playerDetails.getId());
        var requests = AlertsDao.countRequests(playerDetails.getId());

        template.set("feedFriendsOnline", friends.values().stream().filter(MessengerUser::isOnline).collect(Collectors.toList()));
        template.set("feedFriendRequests", requests);

        webConnection.session().delete("friendsOnlineRequest");
    }
}
