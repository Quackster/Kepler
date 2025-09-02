package net.h4bbo.http.kepler.controllers.homes;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.GroupDao;
import net.h4bbo.kepler.dao.mysql.MessengerDao;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.dao.mysql.RoomDao;
import net.h4bbo.kepler.game.groups.Group;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.http.kepler.dao.GroupEditDao;
import net.h4bbo.http.kepler.dao.WidgetDao;
import net.h4bbo.http.kepler.game.homes.Widget;
import net.h4bbo.http.kepler.game.stickers.StickerManager;
import net.h4bbo.http.kepler.game.stickers.StickerType;
import net.h4bbo.http.kepler.util.BBCode;
import net.h4bbo.http.kepler.util.HtmlUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NoteEditorController {
    public static void noteEditor(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        String noteText = webConnection.post().getString("noteText");
        int skin = webConnection.post().getInt("skin");

        if (noteText.length() > 500) {
            noteText = noteText.substring(0, 500);
        }

        Template template = webConnection.template("homes/editor/noteeditor");

        if (skin > 0 && skin < 9) {
            template.set("skin" + skin + "Selected", " selected");
        }

        template.set("noteText", noteText);
        template.render();
    }


    public static void notePreview(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        String noteText = BBCode.format(HtmlUtil.escape(BBCode.normalise(webConnection.post().getString("noteText"))), false);
        int skin = webConnection.post().getInt("skin");

        if (noteText.length() > 500) {
            noteText = noteText.substring(0, 500);
        }

        Template template = webConnection.template("homes/editor/preview");
        template.set("skin", StickerManager.getInstance().getSkin(skin));
        template.set("noteText", noteText);
        template.render();
    }

    public static void search(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        String query = webConnection.get().getString("query");
        int scope = webConnection.get().getInt("scope");
        String type = null;


        List<Pair<String, String>> querySearch = new ArrayList<>();

        switch (scope) {
            case 1:
                type = "habbo";

                List<PlayerDetails> searchedFriends = new ArrayList<>();

                for (int playerId : MessengerDao.search(query)) {
                    searchedFriends.add(PlayerDao.getDetails(playerId));
                }

                searchedFriends.sort(Comparator.comparing(PlayerDetails::getName));

                for (PlayerDetails playerDetails  : searchedFriends.stream().limit(10).collect(Collectors.toList())) {
                    querySearch.add(Pair.of(playerDetails.getName(), String.valueOf(playerDetails.getId())));
                }

                break;
            case 2:
                type = "room";

                var roomList = RoomDao.searchRooms(query, -1, 30);

                for (Room room : roomList.stream().limit(10).collect(Collectors.toList())) {
                    querySearch.add(Pair.of(room.getData().getName(), String.valueOf(room.getData().getId())));
                }

                break;
            default:
                type = "group";

                var groupList = GroupDao.querySearch(query);

                for (Group group : groupList.stream().limit(10).collect(Collectors.toList())) {
                    querySearch.add(Pair.of(group.getName(), String.valueOf(group.getId())));
                }

                break;
        }

        Template tpl = webConnection.template("homes/editor/search");
        tpl.set("querySearch", querySearch);
        tpl.set("type", type);
        tpl.render();
    }

    public static void place(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");

        int skin = webConnection.post().getInt("skin");
        String noteText = webConnection.post().getString("noteText");

        if (noteText.length() > 500) {
            noteText = noteText.substring(0, 500);
        }

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

        Widget widget = WidgetDao.getInventoryWidgets(userId, StickerType.NOTE.getTypeId()).get(0);
        widget.setX(20);
        widget.setY(30);
        widget.setZ(1);

        if (isGroupEdit) {
            widget.setGroupId(webConnection.session().getInt("groupEditSession"));
        }

        widget.setText(noteText);
        widget.setSkinId(skin);
        widget.setPlaced(true);
        widget.save();

        webConnection.headers().put("X-JSON", "" + widget.getId() + "");

        Template tpl = widget.template(webConnection);
        tpl.render();
    }

    public static void stickieEdit(WebConnection webConnection) {
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

        int widgetId = webConnection.post().getInt("stickieId");
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

        webConnection.headers().put("X-JSON", "{\"id\":\"" + widget.getId() + "\",\"cssClass\":\"n_skin_" + widget.getSkin() + "\",\"type\":\"stickie\"}");


        tpl.set("sticker", widget);
        tpl.render();
    }

    public static void stickieDelete(WebConnection webConnection) {
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

        int stickieId = webConnection.post().getInt("stickieId");

        if (isGroupEdit) {
            WidgetDao.delete(stickieId, webConnection.session().getInt("groupEditSession"));
        } else {
            WidgetDao.deleteHomeNote(stickieId, userId);
        }

        webConnection.send("SUCCESS");
    }
}
