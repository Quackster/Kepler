package net.h4bbo.http.kepler.controllers.homes;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.GroupDao;
import net.h4bbo.kepler.game.groups.Group;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.http.kepler.dao.GroupEditDao;
import net.h4bbo.http.kepler.dao.WidgetDao;
import net.h4bbo.http.kepler.game.homes.Widget;

public class WidgetController {
    public static void editWidget(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        boolean isGroupEdit = webConnection.session().contains("groupEditSession");

        if (isGroupEdit) {
            int groupId = webConnection.session().getInt("groupEditSession");
            Group group = GroupDao.getGroup(groupId);

            if (group == null) {
                webConnection.send("");
                return;
            }

            if (!GroupEditDao.hasSession(userId, group.getId())) {
                webConnection.send("");
                return;
            }


        } else {
            if (!webConnection.session().contains("homeEditSession")) {
                webConnection.send("");
                return;
            }
        }

        int widgetId = webConnection.post().getInt("widgetId");
        int skinId = webConnection.post().getInt("skinId");

        Widget widget = null;

        if (isGroupEdit) {
            widget = WidgetDao.getGroupWidget(widgetId, webConnection.session().getInt("groupEditSession"));
        } else {
            widget = WidgetDao.getHomeWidget(userId, widgetId);
        }

        if (widget == null) {
            webConnection.send("");
            return;
        }

        Template tpl = widget.template(webConnection);
        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if ((skinId == 7 || skinId == 8) && !playerDetails.hasClubSubscription()) {
            skinId = 1;
        }

        if (skinId == 9 && playerDetails.getRank().getRankId() < 5) {
            skinId = 1;
        }

        widget.setSkinId(skinId);
        widget.save();

        if (widget.getProduct().isGroupWidget() || widget.getProduct().isHomeWidget()) {
            webConnection.headers().put("X-JSON", "{\"id\":\"" + widget.getId() + "\",\"cssClass\":\"w_skin_" + widget.getSkin() + "\",\"type\":\"widget\"}");
        }


        tpl.set("sticker", widget);
        tpl.render();

    }

    public static void placeSticker(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        boolean isGroupEdit = webConnection.session().contains("groupEditSession");

        if (isGroupEdit) {
            int groupId = webConnection.session().getInt("groupEditSession");
            Group group = GroupDao.getGroup(groupId);

            if (group == null) {
                webConnection.send("");
                return;
            }

            if (!GroupEditDao.hasSession(userId, group.getId())) {
                webConnection.send("");
                return;
            }


        } else {
            if (!webConnection.session().contains("homeEditSession")) {
                webConnection.send("");
                return;
            }
        }

        int widgetId = webConnection.post().getInt("selectedStickerId");
        int zindex = webConnection.post().getInt("zindex");

        if (zindex < 0 || zindex > 100) {
            zindex = 0;
        }

        Widget widget = WidgetDao.getInventoryWidget(userId, widgetId);
        widget.setX(20);
        widget.setY(30);
        widget.setZ(zindex);

        if (isGroupEdit) {
            widget.setGroupId( webConnection.session().getInt("groupEditSession"));
        }

        widget.setPlaced(true);
        widget.save();

        webConnection.headers().put("X-JSON", "[\"" + widget.getId() + "\"]");

        Template tpl = widget.template(webConnection);
        tpl.render();

    }

    public static void placeWidget(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        boolean isGroupEdit = webConnection.session().contains("groupEditSession");

        if (isGroupEdit) {
            int groupId = webConnection.session().getInt("groupEditSession");
            Group group = GroupDao.getGroup(groupId);

            if (group == null) {
                webConnection.send("");
                return;
            }

            if (!GroupEditDao.hasSession(userId, group.getId())) {
                webConnection.send("");
                return;
            }


        } else {
            if (!webConnection.session().contains("homeEditSession")) {
                webConnection.send("");
                return;
            }
        }

        int widgetId = webConnection.post().getInt("widgetId");
        int zindex = webConnection.post().getInt("zindex");

        if (zindex < 0 || zindex > 100) {
            zindex = 0;
        }

        Widget widget = null;

        if (isGroupEdit) {
            widget = WidgetDao.getGroupWidget(widgetId, webConnection.session().getInt("groupEditSession"));
        } else {
            widget = WidgetDao.getHomeWidget(userId, widgetId);
        }

        widget.setX(10);
        widget.setY(10);
        widget.setZ(zindex);

        if (isGroupEdit) {
            widget.setGroupId(webConnection.session().getInt("groupEditSession"));
        }

        widget.setPlaced(true);
        widget.save();

        webConnection.headers().put("X-JSON", "[\"" + widget.getId() + "\"]");

        Template tpl = widget.template(webConnection);

        if (isGroupEdit) {
            tpl.set("group", GroupDao.getGroup(widget.getGroupId()));
        }

        tpl.render();

    }

    public static void removeSticker(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        boolean isGroupEdit = webConnection.session().contains("groupEditSession");

        if (isGroupEdit) {
            int groupId = webConnection.session().getInt("groupEditSession");
            Group group = GroupDao.getGroup(groupId);

            if (group == null) {
                webConnection.send("");
                return;
            }

            if (!GroupEditDao.hasSession(userId, group.getId())) {
                webConnection.send("");
                return;
            }


        } else {
            if (!webConnection.session().contains("homeEditSession")) {
                webConnection.send("");
                return;
            }
        }

        int widgetId = webConnection.post().getInt("stickerId");
        Widget widget = null;

        if (isGroupEdit) {
            widget = WidgetDao.getGroupWidget(widgetId, webConnection.session().getInt("groupEditSession"));
        } else {
            widget = WidgetDao.getHomeWidget(userId, widgetId);
        }

        if (widget == null) {
            webConnection.send("");
            return;
        }

        widget.setX(0);
        widget.setY(0);
        widget.setZ(0);
        widget.setGroupId(0);
        widget.setPlaced(false);
        widget.save();

        webConnection.send("SUCCESS");
    }

    public static void removeWidget(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        boolean isGroupEdit = webConnection.session().contains("groupEditSession");

        if (isGroupEdit) {
            int groupId = webConnection.session().getInt("groupEditSession");
            Group group = GroupDao.getGroup(groupId);

            if (group == null) {
                webConnection.send("");
                return;
            }

            if (!GroupEditDao.hasSession(userId, group.getId())) {
                webConnection.send("");
                return;
            }


        } else {
            if (!webConnection.session().contains("homeEditSession")) {
                webConnection.send("");
                return;
            }
        }

        int widgetId = webConnection.post().getInt("widgetId");

        Widget widget = null;

        if (isGroupEdit) {
            widget = WidgetDao.getGroupWidget(widgetId, webConnection.session().getInt("groupEditSession"));
        } else {
            widget = WidgetDao.getHomeWidget(userId, widgetId);
        }

        if (widget == null) {
            webConnection.send("");
            return;
        }

        if (widget.getProduct().getData().equalsIgnoreCase("groupinfowidget") || widget.getProduct().getData().equalsIgnoreCase("profilewidget")) {
            webConnection.send("");
            return;
        }

        widget.setX(0);
        widget.setY(0);
        widget.setZ(0);

        if (isGroupEdit) {
            widget.setGroupId(webConnection.session().getInt("groupEditSession"));
        }

        widget.setPlaced(false);
        widget.save();

        webConnection.send("SUCCESS");
    }
}
