package net.h4bbo.http.kepler.controllers.groups.discussions;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.http.kepler.dao.GroupDiscussionDao;
import net.h4bbo.http.kepler.game.groups.DiscussionTopic;
import net.h4bbo.http.kepler.util.BBCode;
import net.h4bbo.http.kepler.util.HtmlUtil;

public class DiscussionPreviewController {
    public static void previewtopic(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int groupId = 0;

        try {
            groupId = webConnection.post().getInt("groupId");
        } catch (Exception ex) {

        }

        String topicName = webConnection.post().getString("topicName");
        String topicMessage = webConnection.post().getString("message");

        int userId = webConnection.session().getInt("user.id");
        var displayBadges = GroupDiscussionDao.getDisplayBadges(userId);

        var template = webConnection.template("groups/discussions/previewtopic");
        template.set("topicName", topicName);
        template.set("topicMessage", BBCode.format(HtmlUtil.escape(BBCode.normalise(topicMessage)), false));
        template.set("previewDay", DateUtil.getDate(DateUtil.getCurrentTimeSeconds(), "MMM dd, yyyy").replace("am", "AM").replace("pm", "PM").replace(".", ""));
        template.set("previewTime", DateUtil.getDate(DateUtil.getCurrentTimeSeconds(), "h:mm a").replace("am", "AM").replace("pm", "PM").replace(".", ""));
        template.set("userReplies", GroupDiscussionDao.countUserReplies(userId));

        template.set("hasBadge", false);
        template.set("hasGroup", false);

        if (displayBadges[0] != null) {
            template.set("hasBadge", true);
            template.set("badge", displayBadges[0]);
        }

        if (displayBadges[1] != null) {
            template.set("hasGroup", true);
            template.set("groupId", ((PlayerDetails) template.get("playerDetails")).getFavouriteGroupId());
            template.set("groupBadge", displayBadges[1]);
        }

        template.render();
    }

    public static void previewpost(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int topicId = 0;

        try {
            topicId = webConnection.post().getInt("topicId");
        } catch (Exception ex) {

        }

        int groupId = 0;

        try {
            groupId = webConnection.post().getInt("groupId");
        } catch (Exception ex) {

        }

        DiscussionTopic discussionTopic = GroupDiscussionDao.getDiscussion(groupId, topicId, webConnection.session().getIntOrElse("user.id", 0));

        if (discussionTopic == null) {
            webConnection.redirect("/");
            return;
        }


        String topicMessage = webConnection.post().getString("message");

        int userId = webConnection.session().getInt("user.id");
        var displayBadges = GroupDiscussionDao.getDisplayBadges(userId);

        var template = webConnection.template("groups/discussions/previewpost");
        template.set("postName", "RE: " + discussionTopic.getTopicTitle());
        template.set("postMessage", BBCode.format(HtmlUtil.escape(BBCode.normalise(topicMessage)), false));
        template.set("previewDay", DateUtil.getDate(DateUtil.getCurrentTimeSeconds(), "MMM dd, yyyy").replace("am", "AM").replace("pm","PM").replace(".", ""));
        template.set("previewTime", DateUtil.getDate(DateUtil.getCurrentTimeSeconds(), "h:mm a").replace("am", "AM").replace("pm","PM").replace(".", ""));
        template.set("userReplies", GroupDiscussionDao.countUserReplies(userId));

        template.set("hasBadge", false);
        template.set("hasGroup", false);

        if (displayBadges[0] != null) {
            template.set("hasBadge", true);
            template.set("badge", displayBadges[0]);
        }

        if (displayBadges[1] != null) {
            template.set("hasGroup", true);
            template.set("groupId", ((PlayerDetails)template.get("playerDetails")).getFavouriteGroupId());
            template.set("groupBadge", displayBadges[1]);
        }

        template.render();
    }
}