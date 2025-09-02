package net.h4bbo.http.kepler.controllers.groups;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.config.Settings;
import net.h4bbo.kepler.dao.mysql.GroupDao;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.dao.mysql.RoomDao;
import net.h4bbo.kepler.dao.mysql.TagDao;
import net.h4bbo.kepler.game.groups.Group;
import net.h4bbo.kepler.game.groups.GroupMember;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.http.kepler.dao.GroupEditDao;
import net.h4bbo.http.kepler.dao.WidgetDao;
import net.h4bbo.http.kepler.game.homes.Widget;
import net.h4bbo.http.kepler.util.HomeUtil;
import net.h4bbo.http.kepler.util.XSSUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class GroupController {
    public static void viewGroup(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().contains("authenticated")) {
            return;
        }

        webConnection.session().set("page", "community");

        String match = webConnection.getMatches().get(0);

        String groupAlias = null;
        Group group = null;

        if (StringUtils.isNumeric(match) && webConnection.getRouteRequest().endsWith("/id")) {
            group = GroupDao.getGroup(Integer.parseInt(match));

            if (group == null) {
                webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
                return;
            }

            if (!group.getAlias().isBlank()) {
                webConnection.redirect("/groups/" + group.getAlias());
                return;
            }

        } else if (!webConnection.getRouteRequest().endsWith("/id")) {
            groupAlias = match;
            group = GroupDao.getGroupByAlias(groupAlias);
        }

        if (group == null) {
            webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
            return;
        }

        long sessionTime = -1;
        if (webConnection.session().getBoolean("authenticated")) {
            int userId = webConnection.session().getInt("user.id");
            sessionTime = GroupEditDao.getSession(userId, group.getId());
        }

        if (sessionTime != -1) {
            webConnection.session().delete("homeEditSession");
            webConnection.session().set("groupEditSession", group.getId());
        }

        if (group.getAlias() != null) {
            if (group.getAlias().equalsIgnoreCase("battleball_rebound")) {
                webConnection.session().set("page", "games");
            }

            if (group.getAlias().equalsIgnoreCase("lido")) {
                webConnection.session().set("page", "games");
            }

            if (group.getAlias().equalsIgnoreCase("snow_storm")) {
                webConnection.session().set("page", "games");
            }

            if (group.getAlias().equalsIgnoreCase("wobble_squabble")) {
                webConnection.session().set("page", "games");
            }
        }

        var template = webConnection.template("groups");
        template.set("editMode", sessionTime != -1);
        template.set("group", group);
        template.set("stickers", WidgetDao.getGroupWidgets(group.getId(), true));
        template.set("tags", TagDao.getGroupTags(group.getId()));
        template.set("guestbookSetting", WidgetDao.getGroupWidgets(group.getId()).stream().filter(w -> w.getProduct().getData().equalsIgnoreCase("guestbookwidget")).findFirst().get().getGuestbookState());
        template.set("stickerLimit", HomeUtil.getStickerLimit(true));

        if (sessionTime != -1) {
            template.set("expireMinutes", TimeUnit.SECONDS.toMinutes(sessionTime - DateUtil.getCurrentTimeSeconds()));
        }

        if (group.getRoomId() > 0) {
            Room room = RoomDao.getRoomById(group.getRoomId());

            if (room != null) {
                template.set("room", room);
            }
        }

        template.set("hasMember", false);

        if (webConnection.session().getBoolean("authenticated")) {
            int userId = webConnection.session().getInt("user.id");

            GroupMember groupMember = group.getMember(userId);

            if (groupMember != null) {
                template.set("hasMember", true);
                template.set("groupMember", groupMember);
            }
        }

        template.render();
    }

    public static void startEditingSession(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        String match = webConnection.getMatches().get(0);

        Group group = null;

        if (StringUtils.isNumeric(match)) {
            group = GroupDao.getGroup(Integer.parseInt(match));
        }

        if (group == null) {
            webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
            return;
        }

        if (group.isMember(userId) && group.hasAdministrator(userId)) {
            if (!GroupEditDao.hasSession(userId, group.getId())) {
                GroupEditDao.delete(userId, group.getId());

                GroupEditDao.createSession(userId, group.getId());
                webConnection.session().delete("homeEditSession");
                webConnection.session().set("groupEditSession", group.getId());
            }
        }

        webConnection.redirect(group.generateClickLink());
    }

    public static void cancelEditingSession(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        if (!webConnection.session().contains("groupEditSession")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        int groupId = webConnection.session().getInt("groupEditSession");

        if (GroupEditDao.hasSession(userId, groupId)) {
            GroupEditDao.delete(userId, groupId);
            webConnection.session().delete("homeEditSession");
            webConnection.session().delete("groupEditSession");
        }

        Group group = GroupDao.getGroup(groupId);
        webConnection.redirect(group.generateClickLink());
    }


    public static void saveEditingSession(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        PlayerDetails playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));

        if (playerDetails == null) {
            webConnection.session().delete("user.id");
            webConnection.session().delete("authenticated");
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
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

        List<Widget> groupWidgets = WidgetDao.getGroupWidgets(groupId, true);

        try {
            if (webConnection.post().contains("background")) {
                int backgroundId = Integer.parseInt(webConnection.post().getString("background").split(":")[0]);


                List<Widget> widgetList = WidgetDao.getInventoryWidgets(playerDetails.getId());
                Widget widget = widgetList.stream().filter(w -> w.getId() == backgroundId).findFirst().orElse(null);

                if (widget != null) {
                    group.setBackground(widget.getProduct().getData());
                    group.saveBackground();
                    group.save();
                }
            }

            if (webConnection.post().contains("stickers")) {
                String[] stickerData = webConnection.post().getString("stickers").split(Pattern.quote("/"));

                if (stickerData.length >= HomeUtil.getStickerLimit(true)) {
                    webConnection.send("");
                    return;
                }

                for (String sticker : stickerData) {
                    int stickerId = Integer.parseInt(sticker.split(":")[0]);
                    String[] coordData = sticker.replace(stickerId + ":", "").split(",");

                    int x = Integer.parseInt(coordData[0]);
                    int y = Integer.parseInt(coordData[1]);
                    int z = Integer.parseInt(coordData[2]);

                    Widget widget = groupWidgets.stream().filter(w -> w.getId() == stickerId).findFirst().orElse(null);

                    if (widget != null) {
                        widget.setX(x);
                        widget.setY(y);
                        widget.setZ(z);
                        widget.save();
                    }
                }
            }

            if (webConnection.post().contains("widgets")) {
                String[] stickerData = webConnection.post().getString("widgets").split(Pattern.quote("/"));

                for (String sticker : stickerData) {
                    int stickerId = Integer.parseInt(sticker.split(":")[0]);
                    String[] coordData = sticker.replace(stickerId + ":", "").split(",");

                    int x = Integer.parseInt(coordData[0]);
                    int y = Integer.parseInt(coordData[1]);
                    int z = Integer.parseInt(coordData[2]);

                    Widget widget = groupWidgets.stream().filter(w -> w.getId() == stickerId).findFirst().orElse(null);

                    if (widget != null) {
                        widget.setX(x);
                        widget.setY(y);
                        widget.setZ(z);
                        widget.save();
                    }
                }
            }

            if (webConnection.post().contains("stickienotes")) {
                String[] stickerData = webConnection.post().getString("stickienotes").split(Pattern.quote("/"));

                for (String sticker : stickerData) {
                    int stickerId = Integer.parseInt(sticker.split(":")[0]);
                    String[] coordData = sticker.replace(stickerId + ":", "").split(",");

                    int x = Integer.parseInt(coordData[0]);
                    int y = Integer.parseInt(coordData[1]);
                    int z = Integer.parseInt(coordData[2]);

                    Widget widget = groupWidgets.stream().filter(w -> w.getId() == stickerId).findFirst().orElse(null);

                    if (widget != null) {
                        widget.setX(x);
                        widget.setY(y);
                        widget.setZ(z);
                        widget.save();
                    }
                }
            }
        } catch (Exception ex) {

        }

        GroupEditDao.delete(userId, groupId);

        webConnection.session().delete("homeEditSession");
        webConnection.session().delete("groupEditSession");

        webConnection.send("<script language=\"JavaScript\" type=\"text/javascript\">\n" +
                "waitAndGo('" + group.generateClickLink() + "');\n" +
                "</script>");
    }

    public static void groupinfo(WebConnection webConnection) {
        int groupId = webConnection.post().getInt("groupId");
        Group group = GroupDao.getGroup(groupId);

        if (group == null) {
            webConnection.send("");
            return;
        }

        var template = webConnection.template("homes/widget/habblet/groupinfo");
        template.set("group", group);
        template.render();

    }
}
