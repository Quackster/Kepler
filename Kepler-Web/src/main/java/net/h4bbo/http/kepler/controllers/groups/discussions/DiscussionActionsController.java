package net.h4bbo.http.kepler.controllers.groups.discussions;

import io.netty.handler.codec.http.FullHttpResponse;
import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.dao.mysql.GroupDao;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.groups.Group;
import net.h4bbo.kepler.game.groups.GroupForumType;
import net.h4bbo.kepler.game.groups.GroupMemberRank;
import net.h4bbo.kepler.game.groups.GroupPermissionType;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.dao.GroupDiscussionDao;
import net.h4bbo.http.kepler.game.groups.DiscussionReply;
import net.h4bbo.http.kepler.game.groups.DiscussionTopic;
import net.h4bbo.http.kepler.util.Captcha;
import net.h4bbo.http.kepler.util.XSSUtil;

public class DiscussionActionsController {
    public static void newtopic(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }


        var template = webConnection.template("groups/discussions/newpost");
        template.render();
    }

    public static void savetopic(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        String captcha = webConnection.post().getString("captcha");
        String message = webConnection.post().getString("message");
        String topicName = webConnection.post().getString("topicName");

        int groupId = 0;

        try {
            groupId = webConnection.post().getInt("groupId");
        } catch (Exception ex) {

        }

        if (topicName.isBlank() || message.isBlank()) {
            var template = webConnection.template("groups/discussion_replies");
            template.set("hasMessage", true);
            template.set("message", "Please supply a valid message.");
            template.render();
            return;
        }

        if (!webConnection.session().getString("captcha-text").equals(captcha)) {
            webConnection.session().delete("captcha-text");

            FullHttpResponse httpResponse = ResponseBuilder.create("");
            httpResponse.headers().set("X-JSON", "{\"captchaError\":\"true\"}");
            webConnection.send(httpResponse);
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        var latestMessage = GroupDiscussionDao.getLatestReply(userId);

        if (latestMessage != null && latestMessage.getMessage().startsWith(message)) {
            var template = webConnection.template("groups/discussion_replies");
            template.set("hasMessage", true);
            template.set("message", "Do not spam the forums");
            template.render();
            return;
        }

        Group group = GroupDao.getGroup(groupId);

        if (group == null) {
            webConnection.redirect("/");
            return;
        }
        
        if (group.getForumType() == GroupForumType.PRIVATE ||
            group.getForumPermission() == GroupPermissionType.MEMBER_ONLY ||
            group.getForumPermission() == GroupPermissionType.ADMIN_ONLY) {
            var groupMember = group.getMember(userId);

            if (groupMember == null) {
                webConnection.redirect("/");
                return;
            }

            if (group.getForumPermission() == GroupPermissionType.ADMIN_ONLY) {
                if (groupMember.getMemberRank() != GroupMemberRank.ADMINISTRATOR && groupMember.getMemberRank() != GroupMemberRank.OWNER) {
                    webConnection.redirect("/");
                    return;
                }
            }
        }

        if (topicName.length() > 32) {
            topicName = topicName.substring(0, 32);
        }

        /*int topicId = 1;

        if (WordfilterManager.filterSentence(message).equals(message)) {
            topicId = GroupDiscussionDao.createDiscussion(groupId, userId, topicName);
            GroupDiscussionDao.createReplies(topicId, userId, message);
        }*/

        int topicId = GroupDiscussionDao.createDiscussion(groupId, userId, topicName);
        GroupDiscussionDao.createReplies(topicId, userId, message);

        webConnection.session().delete("captcha-text");
        webConnection.send(group.generateClickLink() + "/discussions/" + topicId + "/id");
    }

    public static void pingsession(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        FullHttpResponse httpResponse = ResponseBuilder.create("");
        httpResponse.headers().set("X-JSON", "{\"privilegeLevel\":\"1\"}");
        webConnection.send(httpResponse);
    }


    public static void opentopicsettings(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int groupId = 0;

        try {
            groupId = webConnection.post().getInt("groupId");
        } catch (Exception ex) {

        }

        int topicId = 0;

        try {
            topicId = webConnection.post().getInt("topicId");
        } catch (Exception ex) {

        }

        DiscussionTopic discussionTopic = GroupDiscussionDao.getDiscussion(groupId, topicId, webConnection.session().getIntOrElse("user.id", 0));

        if (discussionTopic == null) {
            webConnection.redirect("/");
            return;
        }

        var template = webConnection.template("groups/discussions/opentopicsettings");
        template.set("topic", discussionTopic);
        template.render();
    }

    public static void confirm_delete_topic(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        var template = webConnection.template("groups/discussions/confirm_delete_topic");
        template.render();
    }

    public static void deletetopic(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int groupId = 0;

        try {
            groupId = webConnection.post().getInt("groupId");
        } catch (Exception ex) {

        }

        int topicId = 0;

        try {
            topicId = webConnection.post().getInt("topicId");
        } catch (Exception ex) {

        }

        DiscussionTopic discussionTopic = GroupDiscussionDao.getDiscussion(groupId, topicId, webConnection.session().getIntOrElse("user.id", 0));

        if (discussionTopic == null) {
            webConnection.redirect("/");
            return;
        }

        Group group = GroupDao.getGroup(groupId);

        if (group == null) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");

        if (discussionTopic.getCreatorId() != userId) {
            var playerDetails = PlayerDao.getDetails(userId);
            var groupMember = group.getMember(userId);

            if (!Group.hasTopicAdmin(playerDetails.getRank())) {
                if (groupMember == null || groupMember.getMemberRank() == GroupMemberRank.MEMBER) {
                    return;
                }
            }
        }

        GroupDiscussionDao.deleteDiscussion(groupId, topicId);
        webConnection.send("SUCCESS");
    }

    public static void savetopicsettings(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int groupId = 0;
        int topicId = 0;

        try {
            groupId = webConnection.post().getInt("groupId");
            topicId = webConnection.post().getInt("topicId");
        } catch (Exception ex) {

        }


        DiscussionTopic discussionTopic = GroupDiscussionDao.getDiscussion(groupId, topicId, webConnection.session().getIntOrElse("user.id", 0));

        if (discussionTopic == null) {
            webConnection.redirect("/");
            return;
        }

        Group group = GroupDao.getGroup(groupId);

        if (group == null) {
            webConnection.redirect("/");
            return;
        }

        var template = webConnection.template("groups/discussion_replies");
        var playerDetails = (PlayerDetails)template.get("playerDetails");

        int userId = webConnection.session().getInt("user.id");

        if (discussionTopic.getCreatorId() != userId) {
            var groupMember = group.getMember(userId);

            if (!Group.hasTopicAdmin(playerDetails.getRank())) {
                if (groupMember == null || groupMember.getMemberRank() == GroupMemberRank.MEMBER) {
                    return;
                }
            }
        }

        int pageNumber = 1;

        try {
            pageNumber = webConnection.post().getInt("page");
        } catch (Exception ex) {

        }

        if (pageNumber <= 0) {
            pageNumber = 1;
        }

        try {
            String topicTitle = webConnection.post().getString("topicName");

            if (topicTitle.length() > 32) {
                topicTitle = topicTitle.substring(0, 32);
            }

            discussionTopic.setOpen(webConnection.post().getInt("topicClosed") == 0);
            discussionTopic.setStickied(webConnection.post().getInt("topicSticky") == 1);
            discussionTopic.setTopicTitle(topicTitle);
            GroupDiscussionDao.saveDiscussion(discussionTopic);
        } catch (Exception ex) {

        }

        DiscussionController.appendpagedata(template, webConnection, group, discussionTopic, pageNumber);
        template.render();
    }

    public static void updatepost(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int groupId = 0;
        int topicId = 0;
        int postId = 0;
        int pageNumber = 1;

        try {
            groupId = webConnection.post().getInt("groupId");
            topicId = webConnection.post().getInt("topicId");
            postId = webConnection.post().getInt("postId");
            pageNumber = webConnection.post().getInt("page");
        } catch (Exception ex) {

        }

        if (pageNumber <= 0) {
            pageNumber = 1;
        }

        DiscussionTopic discussionTopic = GroupDiscussionDao.getDiscussion(groupId, topicId, webConnection.session().getIntOrElse("user.id", 0));

        if (discussionTopic == null || !discussionTopic.isOpen()) {
            webConnection.redirect("/");
            return;
        }

        DiscussionReply discussionReply = GroupDiscussionDao.getReply(discussionTopic.getId(), postId, webConnection.session().getIntOrElse("user.id", 0));

        if (discussionReply == null) {
            webConnection.redirect("/");
            return;
        }

        Group group = GroupDao.getGroup(groupId);

        if (group == null) {
            webConnection.redirect("/");
            return;
        }

        if (Captcha.matches(webConnection, webConnection.post().getString("captcha"))) {
            webConnection.session().delete("captcha-text");

            FullHttpResponse httpResponse = ResponseBuilder.create("");
            httpResponse.headers().set("X-JSON", "{\"captchaError\":\"true\"}");
            webConnection.send(httpResponse);
            return;
        }

        int userId = webConnection.session().getInt("user.id");

        if (discussionReply.getUserId() != userId) {
            /*var playerDetails = PlayerDao.getDetails(userId);
            var groupMember = group.getMember(userId);

            if (groupMember == null) {
                if (playerDetails.getRank().getRankId() < 6) {
                    return;
                }
            } else {
                if (groupMember.getMemberRank() == GroupMemberRank.MEMBER) {
                    return;
                }
            }*/
            webConnection.redirect("/");
            return;
        }

        var template = webConnection.template("groups/discussion_replies");
        var playerDetails = (PlayerDetails)template.get("playerDetails");

        try {
            String message = webConnection.post().getString("message");
            discussionReply.setMessage(message);

            if (!Group.hasTopicAdmin(playerDetails.getRank())) {
                discussionReply.setEdited(true);
            }

            GroupDiscussionDao.saveReply(discussionReply);
            /*if (WordfilterManager.filterSentence(message).equals(message)) {
                discussionReply.setMessage(message);

                if (!Group.hasTopicAdmin(playerDetails.getRank())) {
                    discussionReply.setEdited(true);
                }

                GroupDiscussionDao.saveReply(discussionReply);
            }*/
        } catch (Exception ex) {

        }

        webConnection.session().delete("captcha-text");

        DiscussionController.appendpagedata(template, webConnection, group, discussionTopic, pageNumber);
        template.render();
    }

    public static void deletepost(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int groupId = 0;
        int topicId = 0;
        int postId = 0;
        int pageNumber = 1;

        try {
            groupId = webConnection.post().getInt("groupId");
            topicId = webConnection.post().getInt("topicId");
            postId = webConnection.post().getInt("postId");
            pageNumber = webConnection.post().getInt("page");
        } catch (Exception ex) {

        }

        if (pageNumber <= 0) {
            pageNumber = 1;
        }

        DiscussionTopic discussionTopic = GroupDiscussionDao.getDiscussion(groupId, topicId, webConnection.session().getIntOrElse("user.id", 0));

        if (discussionTopic == null || !discussionTopic.isOpen()) {
            webConnection.redirect("/?1");
            return;
        }

        DiscussionReply discussionReply = GroupDiscussionDao.getReply(discussionTopic.getId(), postId, webConnection.session().getIntOrElse("user.id", 0));

        if (discussionReply == null) {
            webConnection.redirect("/?2");
            return;
        }

        Group group = GroupDao.getGroup(groupId);

        if (group == null) {
            webConnection.redirect("/?3");
            return;
        }

        var template = webConnection.template("groups/discussion_replies");
        var playerDetails = (PlayerDetails)template.get("playerDetails");

        int userId = webConnection.session().getInt("user.id");

        if (discussionReply.getUserId() != userId) {
            var groupMember = group.getMember(userId);

            if (!Group.hasTopicAdmin(playerDetails.getRank())) {
                if (groupMember == null || groupMember.getMemberRank() == GroupMemberRank.MEMBER) {
                    return;
                }
            }
        }

        try {
            if (discussionReply.getUserId() != userId) {
                GroupDiscussionDao.deleteReply(discussionReply);
            } else {
                if (!Group.hasTopicAdmin(playerDetails.getRank())) {
                    discussionReply.setDeleted(true);
                    GroupDiscussionDao.saveReply(discussionReply);
                } else {
                    GroupDiscussionDao.deleteReply(discussionReply);
                }
            }
        } catch (Exception ex) {

        }

        DiscussionController.appendpagedata(template, webConnection, group, discussionTopic, pageNumber);
        template.render();
    }

    public static void savepost(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int groupId = 0;
        int topicId = 0;

        try {
            groupId = webConnection.post().getInt("groupId");
            topicId = webConnection.post().getInt("topicId");
        } catch (Exception ex) {

        }

        if (!webConnection.session().getString("captcha-text").equals(webConnection.post().getString("captcha"))) {
            webConnection.session().delete("captcha-text");

            FullHttpResponse httpResponse = ResponseBuilder.create("");
            httpResponse.headers().set("X-JSON", "{\"captchaError\":\"true\"}");
            webConnection.send(httpResponse);
            return;
        }

       String message = webConnection.post().getString("message");

        if (message.isBlank()) {
            var template = webConnection.template("groups/discussion_replies");
            template.set("hasMessage", true);
            template.set("message", "Please supply a valid message.");
            template.render();
            return;
        }
        var template = webConnection.template("groups/discussion_replies");
        var playerDetails = (PlayerDetails)template.get("playerDetails");

        DiscussionTopic discussionTopic = GroupDiscussionDao.getDiscussion(groupId, topicId, webConnection.session().getIntOrElse("user.id", 0));

        if (discussionTopic == null || (!discussionTopic.isOpen() && !Group.hasTopicAdmin(playerDetails.getRank()))) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        var latestMessage = GroupDiscussionDao.getLatestReply(userId);

        if (latestMessage != null && latestMessage.getMessage().startsWith(message)) {
            template.set("hasMessage", true);
            template.set("message", "Do not spam the forums");
            template.render();
            return;
        }

        Group group = GroupDao.getGroup(groupId);

        if (group == null) {
            webConnection.redirect("/");
            return;
        }

        if (group.getForumType() == GroupForumType.PRIVATE) {
            var groupMember = group.getMember(userId);

            if (groupMember == null) {
                webConnection.redirect("/");
                return;
            }
        }

        webConnection.session().delete("captcha-text");

        //if (WordfilterManager.filterSentence(message).equals(message)) {
            GroupDiscussionDao.createReplies(topicId, userId, message);
        //}

        int limit = GameConfiguration.getInstance().getInteger("discussions.replies.per.page");
        int replyCount = GroupDiscussionDao.countReplies(discussionTopic.getId());
        int pages = replyCount > 0 ? (int) Math.ceil((double) replyCount / (double) limit) : 1;

        DiscussionController.appendpagedata(template, webConnection, group, discussionTopic, pages);
        template.render();
    }
}
