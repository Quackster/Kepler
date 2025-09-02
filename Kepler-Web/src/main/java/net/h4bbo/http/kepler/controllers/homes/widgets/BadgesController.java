package net.h4bbo.http.kepler.controllers.homes.widgets;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.game.badges.Badge;
import net.h4bbo.http.kepler.dao.WidgetDao;
import net.h4bbo.http.kepler.game.homes.Widget;

import java.util.List;

public class BadgesController {
    public static void badgepaging(WebConnection webConnection) {
        int widgetId = webConnection.post().getInt("widgetId");
        int pageNumber = webConnection.post().getInt("pageNumber");

        Widget widget = WidgetDao.getWidget(widgetId);

        if (widget == null) {
            webConnection.send("");
            return;
        }

        var pages = widget.getBadgeList();
        boolean showLast = true;

        List<Badge> badgeList = pages.get(0);

        if (pageNumber > pages.size()) {
            pageNumber = pages.size();
        }

        if (pageNumber >= pages.size()) {
            showLast = false;
        }

        if (pageNumber <= 0) {
            pageNumber = 1;
        }

        if (pages.containsKey(pageNumber - 1)) {
            badgeList = pages.get(pageNumber - 1);
        }

        var template = webConnection.template("homes/widget/habblet/badgepaging");
        template.set("sticker", widget);
        template.set("pages", pages.size());
        template.set("showLast", showLast);
        template.set("badgeList", badgeList);
        template.set("currentPage", pageNumber);
        template.render();
    }
}
