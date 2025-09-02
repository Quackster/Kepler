package net.h4bbo.http.kepler.controllers.groups;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.dao.mysql.GroupDao;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.groups.Group;
import net.h4bbo.kepler.server.rcon.messages.RconHeader;
import net.h4bbo.http.kepler.util.RconUtil;

import java.util.HashMap;

public class GroupFavouriteController {
    public static void confirmselectfavourite(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int groupId = webConnection.post().getInt("groupId");
        String groupName = GroupDao.getGroupName(groupId);

        if (groupName == null) {
            webConnection.send("");
            return;
        }

        var template = webConnection.template("groups/favourite/confirm_select_favourite");
        template.set("groupName", groupName);
        template.render();
    }

    public static void selectfavourite(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int groupId = webConnection.post().getInt("groupId");
        int userId = webConnection.session().getInt("user.id");

        Group group = GroupDao.getGroup(groupId);

        if (group == null || !group.isMember(userId)) {
            webConnection.send("");
            return;
        }

        PlayerDao.saveFavouriteGroup(userId, groupId);
        RconUtil.sendCommand(RconHeader.REFRESH_GROUP_PERMS, new HashMap<>() {{
            put("userId", String.valueOf(userId));
        }});

        webConnection.send("OK");
    }

    public static void confirmdeselectfavourite(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        var template = webConnection.template("groups/favourite/confirm_deselect_favourite");
        template.render();
    }

    public static void deselectfavourite(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int groupId = webConnection.post().getInt("groupId");
        int userId = webConnection.session().getInt("user.id");

        Group group = GroupDao.getGroup(groupId);

        if (group == null || !group.isMember(userId)) {
            webConnection.send("");
            return;
        }

        PlayerDao.saveFavouriteGroup(userId, 0);

        RconUtil.sendCommand(RconHeader.REFRESH_GROUP_PERMS, new HashMap<>() {{
            put("userId", String.valueOf(userId));
        }});

        webConnection.send("OK");
    }
}
