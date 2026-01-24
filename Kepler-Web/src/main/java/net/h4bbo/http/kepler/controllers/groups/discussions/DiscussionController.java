package net.h4bbo.http.kepler.controllers.groups.discussions;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import org.alexdev.duckhttpd.util.config.Settings;
import net.h4bbo.kepler.dao.mysql.GroupDao;
import net.h4bbo.kepler.game.groups.Group;
import net.h4bbo.kepler.game.groups.GroupForumType;
import net.h4bbo.kepler.game.groups.GroupMember;
import net.h4bbo.kepler.game.groups.GroupMemberRank;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.dao.GroupDiscussionDao;
import net.h4bbo.http.kepler.dao.ReplyDao;
import net.h4bbo.http.kepler.game.groups.DiscussionReply;
import net.h4bbo.http.kepler.game.groups.DiscussionTopic;
import net.h4bbo.http.kepler.util.XSSUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class DiscussionController {
    public static void viewDiscussion(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        webConnection.session().set("page", "community");
        String match = webConnection.getMatches().get(0);

        int discussionId = 0;

        try {
            discussionId = Integer.parseInt(webConnection.getMatches().get(1));
        } catch (Exception ex) {

        }

        boolean hasPageSpecified = false;
        int pageNumber = 1;

        try {
            pageNumber = Integer.parseInt(webConnection.getMatches().get(2));
            hasPageSpecified = true;
        } catch (Exception ex) {

        }

        String groupAlias;
        Group group;

        if (StringUtils.isNumeric(match) && webConnection.getRouteRequest().contains("/id/discussions")) {
            group = GroupDao.getGroup(Integer.parseInt(match));

            if (group == null) {
                webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
                return;
            }

            if (!group.getAlias().isBlank()) {
                webConnection.redirect("/groups/" + group.getAlias() + "/discussions/" + discussionId + "/id" + (hasPageSpecified ? "/page/" + pageNumber : ""));
                return;
            }

        } else {
            groupAlias = match;
            group = GroupDao.getGroupByAlias(groupAlias);
        }

        if (group == null) {
            webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
            return;
        }

        DiscussionTopic discussionTopic = GroupDiscussionDao.getDiscussion(group.getId(), discussionId, webConnection.session().getIntOrElse("user.id", 0));

        if (discussionTopic == null) {
            webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
            return;
        }


        var template = webConnection.template("groups/discussion");

        if (pageNumber <= 0) {
            pageNumber = 1;
        }

        if (!webConnection.session().contains("hasViewedDiscussion" + discussionId)) {
            webConnection.session().set("hasViewedDiscussion" + discussionId, true);
            GroupDiscussionDao.incrementViews(discussionId);
        }

        appendpagedata(template, webConnection, group, discussionTopic, pageNumber);
        template.render();
    }

    public static void appendpagedata(Template template, WebConnection webConnection, Group group, DiscussionTopic discussionTopic, int pageNumber) {
        boolean loggedIn = webConnection.session().getBoolean("authenticated");
        boolean hasTopicAdmin = false;

        if (loggedIn) {
            var playerDetails = (PlayerDetails)template.get("playerDetails");
            hasTopicAdmin = Group.hasTopicAdmin(playerDetails.getRank());
        }

        template.set("group", group);
        template.set("hasMember", false);
        template.set("hasMessage", false);
        template.set("canViewForum", group.getForumType() == GroupForumType.PUBLIC);
        template.set("canReplyForum", false);//(loggedIn && (!group.canForumPost(playerDetails.getId()))));

        int userId = webConnection.session().getIntOrElse("user.id", 0);

        if (webConnection.session().getBoolean("authenticated")) {
            GroupMember groupMember = group.getMember(userId);

            template.set("canViewForum", (loggedIn && group.canViewForum(groupMember)));
            template.set("canReplyForum", (loggedIn && group.canReplyForum(groupMember)));

            if (groupMember != null) {
                template.set("hasMember", true);
                template.set("groupMember", groupMember);

                if (!hasTopicAdmin) {
                    hasTopicAdmin = (groupMember.getMemberRank() == GroupMemberRank.ADMINISTRATOR || groupMember.getMemberRank() == GroupMemberRank.OWNER);
                }
            }
        }

        if (!discussionTopic.isOpen()) {
            template.set("canReplyForum", false);
        }

        if (!((boolean)template.get("canViewForum"))) {
            template.set("hasMessage", true);
            template.set("message", "View forums denied. Please check that you are logged in and have the appropriate rights to view the forums. If you are logged in and still can't view the forums, the group may be private. If so, you need to join the group in order to view the forums. ");
            return;
        }

        int firstReply = GroupDiscussionDao.getFirstReply(discussionTopic.getId());
        template.set("firstReply", firstReply);

        int limit = GameConfiguration.getInstance().getInteger("discussions.replies.per.page");

        int replyCount = GroupDiscussionDao.countReplies(discussionTopic.getId());
        int pages = replyCount > 0 ? (int) Math.ceil((double) replyCount / (double) limit) : 1;
        List<DiscussionReply> replyList = GroupDiscussionDao.getReplies(discussionTopic.getId(), pageNumber, limit, webConnection.session().getIntOrElse("user.id", 0));

        if (userId > 0) {
            var isNew = replyList.stream().filter(DiscussionReply::isNew).collect(Collectors.toList());

            if (isNew.size() > 0) {
                ReplyDao.read(userId, replyList.stream().filter(DiscussionReply::isNew).collect(Collectors.toList()));
            }
        }

        for (int i = 1; i < 3 + 1; i++) {
            int newPage = pageNumber - i;

            if (newPage >= 1) {
                template.set("previousPage" + i, pageNumber - i);
            } else {
                template.set("previousPage" + i, -1);
            }
        }

        for (int i = 1; i < 3 + 1; i++) {
            int newPage = pageNumber + i;

            if (newPage > 1 && newPage <= pages) {
                template.set("nextPage" + i, pageNumber + i);
            } else {
                template.set("nextPage" + i, -1);
            }
        }

        template.set("currentPage", pageNumber);
        template.set("pages", pages);
        template.set("replyList", replyList);
        template.set("discussionId", discussionTopic.getId());
        template.set("discussionTopic", discussionTopic);
        template.set("hasTopicAdmin", hasTopicAdmin);

    }
}
