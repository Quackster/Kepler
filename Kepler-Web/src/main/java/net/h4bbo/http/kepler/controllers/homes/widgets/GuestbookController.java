package net.h4bbo.http.kepler.controllers.homes.widgets;

import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.GroupDao;
import net.h4bbo.kepler.dao.mysql.PlayerStatisticsDao;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;
import net.h4bbo.kepler.game.wordfilter.WordfilterManager;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.http.kepler.dao.GuestbookDao;
import net.h4bbo.http.kepler.dao.WidgetDao;
import net.h4bbo.http.kepler.game.homes.GuestbookEntry;
import net.h4bbo.http.kepler.game.homes.Widget;
import net.h4bbo.http.kepler.game.stickers.StickerType;
import net.h4bbo.http.kepler.util.BBCode;
import net.h4bbo.http.kepler.util.HtmlUtil;

import java.util.concurrent.ThreadLocalRandom;

public class GuestbookController {
    public static void preview(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        String message = BBCode.format(HtmlUtil.escape(BBCode.normalise(webConnection.post().getString("message"))), false);

        if (message.length() > 200) {
            message = message.substring(0, 200);
        }

        Template template = webConnection.template("homes/widget/guestbook/preview");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        template.set("message", message);
        template.set("author", playerDetails);
        template.set("formattedDate", DateUtil.getFriendlyDate(DateUtil.getCurrentTimeSeconds()));
        template.render();
    }

    public static void add(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int widgetId = webConnection.post().getInt("widgetId");
        Widget widget = WidgetDao.getWidget(widgetId);

        if (widget == null || !widget.getProduct().getData().toLowerCase().equals("guestbookwidget")) {
            webConnection.send("");
            return;
        }

        String message = webConnection.post().getString("message");

        if (message.length() > 200) {
            message = message.substring(0, 200);
        }

        if (!widget.isPlaced()) {
            webConnection.send("");
            return;
        }

        if (!widget.isPostingAllowed(webConnection.session().getInt("user.id"))) {
            webConnection.send("");
            return;
        }

        int homeId = 0;
        int groupId = 0;

        if (widget.getProduct().getType() == StickerType.GROUP_WIDGET) {
            groupId = widget.getGroupId();
        } else if (widget.getProduct().getType() == StickerType.HOME_WIDGET) {
            homeId = widget.getUserId();

            if (homeId != webConnection.session().getInt("user.id")) {
                PlayerStatisticsDao.incrementStatistic(homeId, PlayerStatistic.GUESTBOOK_UNREAD_MESSAGES, 1);
                //HomesDao.incrementUnreadMessages(homeId);
            }
        }
        GuestbookEntry guestbookEntry = null;

        if (WordfilterManager.filterSentence(message).equals(message)) {
            guestbookEntry = GuestbookDao.create(webConnection.session().getInt("user.id"), homeId, groupId, message);
        } else {
            guestbookEntry = new GuestbookEntry(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE), webConnection.session().getInt("user.id"), homeId, groupId, message, DateUtil.getCurrentTimeSeconds());
        }

        Template template = webConnection.template("homes/widget/guestbook/add");
        template.set("entry", guestbookEntry);
        template.set("sticker", widget);
        template.set("canDeleteEntries", widget.canDeleteEntries(webConnection.session().getInt("user.id")));
        template.render();
    }

    public static void remove(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }
        int entryId = -1;
        int widgetId = -1;
        
        try {
            entryId = webConnection.post().getInt("entryId");
            widgetId = webConnection.post().getInt("widgetId");
        } catch (Exception ex) {

        }

        Widget widget = WidgetDao.getWidget(widgetId);

        if (widget == null || !widget.getProduct().getData().toLowerCase().equals("guestbookwidget") || !widget.isPlaced()) {
            webConnection.send("");
            return;
        }

        GuestbookEntry entry = GuestbookDao.getEntry(entryId);

        if (entry == null || !widget.canDeleteEntries(webConnection.session().getInt("user.id")) && entry.getUserId() != webConnection.session().getInt("user.id")) {
            webConnection.send("");
            return;
        }

        int homeId = 0;
        int groupId = 0;

        if (widget.getProduct().getType() == StickerType.GROUP_WIDGET) {
            groupId = widget.getGroupId();
        } else if (widget.getProduct().getType() == StickerType.HOME_WIDGET) {
            homeId = widget.getUserId();
        }

        GuestbookDao.remove(entryId, homeId, groupId);

        Template template = webConnection.template("homes/widget/guestbook_widget");
        template.set("editMode", webConnection.session().contains("homeEditSession") || webConnection.session().contains("groupEditSession"));
        template.set("sticker", widget);
        template.render();
    }

    public static void configure(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        int widgetId = webConnection.post().getInt("widgetId");

        Widget widget = WidgetDao.getWidget(widgetId);

        if (widget == null || !widget.getProduct().getData().toLowerCase().equals("guestbookwidget") || !widget.isPlaced()) {
            webConnection.send("");
            return;
        }

        int ownerId = 0;

        if (widget.getProduct().getType() == StickerType.GROUP_WIDGET) {
            ownerId = GroupDao.getGroupOwner(widget.getGroupId());
        } else if (widget.getProduct().getType() == StickerType.HOME_WIDGET) {
            ownerId = widget.getUserId();
        }

        if (ownerId != userId) {
            webConnection.send("");
            return;
        }

        if (widget.getGuestbookState().equalsIgnoreCase("private")) {
            widget.setExtraData("public");
        } else {
            widget.setExtraData("private");
        }

        widget.save();

        webConnection.send(ResponseBuilder.create("text/javascript", "var el = $(\"guestbook-type\");\n" +
                "if (el) {\n" +
                "\tif (el.hasClassName(\"public\")) {\n" +
                "\t\tel.className = \"private\";\n" +
                "\t\tnew Effect.Pulsate(el,\n" +
                "\t\t\t{ duration: 1.0, afterFinish : function() { Element.setOpacity(el, 1); } }\n" +
                "\t\t);\t\t\t\t\t\t\n" +
                "\t} else {\t\t\t\t\t\t\n" +
                "\t\tnew Effect.Pulsate(el,\n" +
                "\t\t\t{ duration: 1.0, afterFinish : function() { Element.setOpacity(el, 0); el.className = \"public\"; } }\n" +
                "\t\t);\t\t\t\t\t\t\n" +
                "\t}\n" +
                "}"));

    }
}
