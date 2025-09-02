package net.h4bbo.http.kepler.controllers.groups;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import org.alexdev.duckhttpd.util.config.Settings;
import net.h4bbo.kepler.dao.mysql.GroupDao;
import net.h4bbo.kepler.game.groups.Group;
import net.h4bbo.kepler.game.groups.GroupForumType;
import net.h4bbo.kepler.game.groups.GroupMember;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.dao.GroupDiscussionDao;
import net.h4bbo.http.kepler.game.groups.DiscussionTopic;
import net.h4bbo.http.kepler.util.XSSUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class GroupDiscussionsController {
    public static void viewDiscussions(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        webConnection.session().set("page", "community");
        String match = webConnection.getMatches().get(0);

        String groupAlias = null;
        Group group = null;

        if (StringUtils.isNumeric(match) && webConnection.getRouteRequest().endsWith("/id/discussions")) {
            group = GroupDao.getGroup(Integer.parseInt(match));

            if (group == null) {
                webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
                return;
            }

            if (!group.getAlias().isBlank()) {
                webConnection.redirect("/groups/" + group.getAlias() + "/discussions");
                return;
            }

        } else if (webConnection.getRouteRequest().endsWith("/discussions")) {
            groupAlias = match;
            group = GroupDao.getGroupByAlias(groupAlias);
        }

        if (group == null) {
            webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
            return;
        }

        var template = webConnection.template("groups/view_discussions");
        template.set("group", group);
        render(webConnection, group, template, 1);
        template.render();
    }

    public static void viewDiscussionsPage(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        webConnection.session().set("page", "community");

        String match = webConnection.getMatches().get(0);

        String groupAlias = null;
        Group group = null;

        if (StringUtils.isNumeric(match) && webConnection.getRouteRequest().contains("/id/discussions")) {
            group = GroupDao.getGroup(Integer.parseInt(match));

            if (group == null) {
                webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
                return;
            }

            if (!group.getAlias().isBlank()) {
                webConnection.redirect("/groups/" + group.getAlias() + "/discussions");
                return;
            }

        } else if (webConnection.getRouteRequest().contains("/discussions") && !webConnection.getRouteRequest().contains("id/")) {
            groupAlias = match;
            group = GroupDao.getGroupByAlias(groupAlias);
        }

        if (group == null) {
            webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
            return;
        }

        int page = 1;

        try {
            page = Integer.parseInt(webConnection.getMatches().get(1));
        } catch (Exception ex) {

        }

        var template = webConnection.template("groups/view_discussions");
        template.set("group", group);
        render(webConnection, group, template, page);
        template.render();
    }

    private static void render(WebConnection webConnection, Group group, Template template, int pageNumber) {
        boolean loggedIn = webConnection.session().getBoolean("authenticated");
        template.set("hasMember", false);
        template.set("canViewForum", group.getForumType() == GroupForumType.PUBLIC);
        template.set("canPostForum", false);//(loggedIn && (!group.canForumPost(playerDetails.getId()))));

        if (webConnection.session().getBoolean("authenticated")) {
            int userId = webConnection.session().getInt("user.id");

            GroupMember groupMember = group.getMember(userId);
            template.set("canPostForum", group.canForumPost(groupMember));

            if (groupMember != null) {
                template.set("hasMember", true);
                template.set("groupMember", groupMember);
                template.set("canViewForum", (loggedIn && group.canViewForum(groupMember)));
            }
        }

        if (pageNumber <= 0) {
            pageNumber = 1;
        }

        List<DiscussionTopic> discussionTopics = new ArrayList<>();

        int limit = GameConfiguration.getInstance().getInteger("discussions.per.page");
        int discussionCount = 0;
        int pages = 1;

        if ((boolean)template.get("canViewForum")) {
            discussionCount = GroupDiscussionDao.countDiscussions(group.getId());
            pages = discussionCount > 0 ? (int) Math.ceil((double) discussionCount / (double) limit) : 1;
            discussionTopics = GroupDiscussionDao.getDiscussions(group.getId(), pageNumber, limit, webConnection.session().getIntOrElse("user.id", 0));
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
        template.set("discussionTopics", discussionTopics);
    }

}
