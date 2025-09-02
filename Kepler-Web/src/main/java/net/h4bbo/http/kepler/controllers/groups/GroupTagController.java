package net.h4bbo.http.kepler.controllers.groups;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.dao.mysql.GroupDao;
import net.h4bbo.kepler.dao.mysql.TagDao;
import net.h4bbo.kepler.game.groups.Group;
import net.h4bbo.kepler.game.wordfilter.WordfilterManager;
import net.h4bbo.kepler.util.StringUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;

import java.util.List;

public class GroupTagController {
    public static void addGroupTag(WebConnection webConnection) {
        int userId = webConnection.session().getInt("user.id");

        if (userId < 1) {
            webConnection.send("");
            return;
        }

        int groupId = webConnection.post().getInt("groupId");
        Group group = GroupDao.getGroup(groupId);

        if (group == null) {
            webConnection.send("");
            return;
        }

        if (group.getOwnerId() != userId) {
            webConnection.send("");
            return;
        }

        var tagList = TagDao.getGroupTags(groupId);

        if (tagList.size() >= GameConfiguration.getInstance().getInteger("max.tags.groups")) {
            webConnection.send("taglimit");
            return;
        }

        String tag = StringUtil.isValidTag(webConnection.post().getString("tagName"), 0, 0, groupId);

        if (tag == null) {
            webConnection.send("invalidtag");
            return;
        }

        if (WordfilterManager.filterSentence(tag).equals(tag)) {
            StringUtil.addTag(tag, 0, 0, groupId);
        }

        webConnection.send("valid");
    }

    public static void removeGroupTag(WebConnection webConnection) {
        int userId = webConnection.session().getInt("user.id");

        if (userId < 1) {
            webConnection.send("");
            return;
        }

        int groupId = webConnection.post().getInt("groupId");
        Group group = GroupDao.getGroup(groupId);

        if (group == null) {
            webConnection.send("");
            return;
        }

        if (group.getOwnerId() != userId) {
            webConnection.send("");
            return;
        }

        TagDao.removeTag(0, 0, groupId, webConnection.post().getString("tagName"));
        List<String> groupTags = TagDao.getGroupTags(groupId);

        var template = webConnection.template("groups/habblet/listgrouptags");
        template.set("tags", groupTags);
        template.set("group", group);
        template.render();
    }

    public static void listGroupTag(WebConnection webConnection) {
        int userId = webConnection.session().getInt("user.id");

        if (userId < 1) {
            webConnection.send("");
            return;
        }

        int groupId = webConnection.post().getInt("groupId");
        Group group = GroupDao.getGroup(groupId);

        if (group == null) {
            webConnection.send("");
            return;
        }

        List<String> groupTags = TagDao.getGroupTags(groupId);

        var template = webConnection.template("groups/habblet/listgrouptags");
        template.set("tags", groupTags);
        template.set("group", group);
        template.render();
    }

}
