package net.h4bbo.http.kepler.controllers.homes.widgets;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.http.kepler.dao.RatingDao;
import net.h4bbo.http.kepler.dao.WidgetDao;
import net.h4bbo.http.kepler.game.homes.Widget;

public class RateController {
    public static void rate(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");

        int widgetId = -1;
        int rating = -1;

        try {
            widgetId = webConnection.get().getInt("ratingId");
            rating = webConnection.get().getInt("givenRate");
        } catch (Exception ex) {

        }

        if (rating < 1 || rating > 5) {
            webConnection.send("");
            return;
        }

        Widget widget = WidgetDao.getWidget(widgetId);

        if (widget == null) {
            webConnection.send("");
            return;
        }

        int homeId = widget.getUserId();

        if (homeId == userId) {
            webConnection.send("");
            return;
        }

        if (RatingDao.hasRated(userId, homeId)) {
            webConnection.send("");
            return;
        }

        RatingDao.rate(userId, homeId, rating);

        var template = webConnection.template("homes/widget/habblet/rate");
        template.set("sticker", widget);
        template.render();
    }

    public static void resetRating(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        int widgetId = webConnection.get().getInt("ratingId");

        Widget widget = WidgetDao.getWidget(widgetId);

        if (widget == null) {
            webConnection.send("");
            return;
        }

        int homeId = widget.getUserId();

        if (homeId != userId) {
            webConnection.send("");
            return;
        }

        RatingDao.deleteRating(homeId);

        var template = webConnection.template("homes/widget/habblet/rate");
        template.set("sticker", widget);
        template.render();
    }
}
