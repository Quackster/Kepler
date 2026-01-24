package net.h4bbo.http.kepler.controllers.homes.widgets;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.messenger.MessengerUser;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.http.kepler.dao.WidgetDao;
import net.h4bbo.http.kepler.game.homes.Widget;

import java.util.List;

public class FriendsWidgetController {
    public static void friendsearchpaging(WebConnection webConnection) {
        int widgetId = webConnection.post().getInt("widgetId");
        int pageNumber = 1;

        try {
            pageNumber = webConnection.post().getInt("pageNumber");
        } catch (Exception ex) {

        }

        if (pageNumber <= 0) {
            pageNumber = 1;
        }

        String searchString = webConnection.post().getString("searchString");

        Widget widget = WidgetDao.getWidget(widgetId);

        if (widget == null) {
            webConnection.send("");
            return;
        }

        var pages = searchString.isBlank() ? widget.getFriendsPages() : widget.getFriendsPagesSearch(searchString);
        List<MessengerUser> friendsList = widget.getFriendsList(searchString, pageNumber);

        var template = webConnection.template("homes/widget/habblet/friendsearchpaging");
        template.set("sticker", widget);
        template.set("pages", pages);
        template.set("friends", widget.getFriendsAmount());
        template.set("friendsList", friendsList);
        template.set("currentPage", pageNumber);
        template.render();

    }

    public static void avatarinfo(WebConnection webConnection) {
        int userId = webConnection.post().getInt("anAccountId");
        PlayerDetails playerDetails = PlayerDao.getDetails(userId);

        if (playerDetails == null) {
            webConnection.send("");
            return;
        }

        var template = webConnection.template("homes/widget/habblet/avatarinfo");
        template.set("avatar", playerDetails);
        template.render();
    }
}
