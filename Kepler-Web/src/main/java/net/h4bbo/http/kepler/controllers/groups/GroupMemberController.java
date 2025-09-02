package net.h4bbo.http.kepler.controllers.groups;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.dao.mysql.GroupDao;
import net.h4bbo.kepler.dao.mysql.GroupMemberDao;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.groups.Group;
import net.h4bbo.kepler.game.groups.GroupMember;
import net.h4bbo.kepler.game.groups.GroupMemberRank;
import net.h4bbo.kepler.game.player.PlayerRank;
import net.h4bbo.kepler.server.rcon.messages.RconHeader;
import net.h4bbo.http.kepler.util.RconUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;

public class GroupMemberController {
    public static void join(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int groupId = webConnection.post().getInt("groupId");
        Group group = GroupDao.getGroup(groupId);

        if (group == null) {
            webConnection.send("");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        var playerDetails = PlayerDao.getDetails(userId);

        if (playerDetails == null || group.isMember(userId) || group.isPendingMember(userId) || group.getGroupType() == 2) {
            webConnection.send("");
            return;
        }

        if (group.getGroupType() == 0 || group.getGroupType() == 3 || playerDetails.getRank().getRankId() >= PlayerRank.MODERATOR.getRankId()) {
            var template = webConnection.template("groups/member/member_added");
            template.render();

            /*webConnection.send("<p>\n" +
                    "You have now joined this group </p>\n" +
                    "\n" +
                    "<p>\n" +
                    "<a href=\"#\" class=\"new-button\" id=\"group-action-ok\"><b>Ok</b><i></i></a>\n" +
                    "</p>\n" +
                    "\n" +
                    "<div class=\"clear\"></div>"));*/
        }

        if (group.getGroupType() == 1) {
            var template = webConnection.template("groups/member/member_added_request");
            template.render();
            /*webConnection.send("<p>\n" +
                    "Your membership request has been sent.</p>\n" +
                    "\n" +
                    "<p>\n" +
                    "<a href=\"#\" class=\"new-button\" id=\"group-action-ok\"><b>Ok</b><i></i></a>\n" +
                    "</p>\n" +
                    "\n" +
                    "<div class=\"clear\"></div>"));*/
        }

        if (playerDetails.getRank().getRankId() >= PlayerRank.MODERATOR.getRankId()) {
            GroupMemberDao.addMember(userId, groupId, false);
        } else {
            GroupMemberDao.addMember(userId, groupId, group.getGroupType() == 1);
        }
    }

    public static void confirmLeave(WebConnection webConnection) {
        var template = webConnection.template("groups/member/confirm_leave");
        template.render();

        /*
        webConnection.send("<p>\n" +

                "Are you sure you want to leave this group?</p>\n" +
                "\n" +
                "\n" +
                "<p>\n" +
                "<a href=\"#\" class=\"new-button\" id=\"group-action-cancel\"><b>Cancel</b><i></i></a>\n" +
                "<a href=\"#\" class=\"new-button\" id=\"group-action-ok\"><b>Ok</b><i></i></a>\n" +
                "</p>\n" +
                "\n" +
                "<div class=\"clear\"></div>"));*/
    }

    public static void leave(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int groupId = webConnection.post().getInt("groupId");
        Group group = GroupDao.getGroup(groupId);

        if (group == null) {
            webConnection.send("");
            return;
        }

        int userId = webConnection.session().getInt("user.id");

        if (group.isMember(userId)) {
            GroupMember groupMember = group.getMember(userId);

            GroupMemberDao.deleteMember(userId, groupId);

            if (groupMember.getUser().getFavouriteGroupId() == group.getId()) {
                PlayerDao.saveFavouriteGroup(userId, 0);

                RconUtil.sendCommand(RconHeader.REFRESH_GROUP_PERMS, new HashMap<>() {{
                    put("userId", String.valueOf(userId));
                }});
            }
        }

        /*webConnection.send("<p>\n" +
                "Are you sure you want to leave this group?</p>\n" +
                "\n" +
                "\n" +
                "<p>\n" +
                "<a href=\"#\" class=\"new-button\" id=\"group-action-cancel\"><b>Cancel</b><i></i></a>\n" +
                "<a href=\"#\" class=\"new-button\" id=\"group-action-ok\"><b>Ok</b><i></i></a>\n" +
                "</p>\n" +
                "\n" +
                "<div class=\"clear\"></div>"));*/
        var template = webConnection.template("groups/member/leave");
        template.set("groupId", groupId);
        template.render();
    }

    public static void memberlist(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int groupId = webConnection.post().getInt("groupId");
        int userId = webConnection.session().getInt("user.id");

        Group group = GroupDao.getGroup(groupId);

        if (group == null || !group.hasAdministrator(userId)) {
            webConnection.send("");
            return;
        }

        int pageNumber = 1;

        try {
            pageNumber = webConnection.post().getInt("pageNumber");
        } catch (Exception ex) {

        }

        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        
        int pendingMembers = GroupMemberDao.countMembers(group.getId(), true);
        int groupMembers = GroupMemberDao.countMembers(group.getId(), false);
        
        int limit = 12;
        boolean isPending = webConnection.post().getBoolean("pending");
        List<GroupMember> groupMemberList = GroupMemberDao.getMembers(groupId, isPending, "", pageNumber, limit);

        int memberCount = isPending ? pendingMembers : groupMembers;
        int pages = memberCount > 0 ? (int) Math.ceil((double)memberCount / (double)limit) : 0;

        if (pages == 0) {
            pages = 1;
        }

        GroupMember selfMember = group.getMember(userId);
        webConnection.headers().put("X-JSON", "{\"pending\":\"Pending members (" + pendingMembers + ")\",\"members\":\"Members (" + groupMembers + ")\"}");

        var template = webConnection.template("groups/memberlist");
        template.set("pages", pages);
        template.set("memberList", groupMemberList);
        template.set("currentPage", pageNumber);
        template.set("selfMember", selfMember);
        template.render();


    }

    public static void confirmRevokeRights(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int targetIds = webConnection.post().getArray("targetIds").size();

        var template = webConnection.template("groups/member/confirm_revoke_rights");
        template.set("targetIds", targetIds);
        template.render();
    }

    public static void revokeRights(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int groupId = webConnection.post().getInt("groupId");
        int userId = webConnection.session().getInt("user.id");

        Group group = GroupDao.getGroup(groupId);

        if (group == null || group.getMember(userId).getMemberRank() != GroupMemberRank.OWNER) {
            webConnection.send("");
            return;
        }

        List<String> data = webConnection.post().getArray("targetIds");

        for (String user : data) {
            if (!StringUtils.isNumeric(user)) {
                continue;
            }

            int memberId = Integer.parseInt(user);
            GroupMember groupMember = group.getMember(memberId);

            if (groupMember == null || groupMember.getMemberRank() == GroupMemberRank.OWNER) {
                continue;
            }

            GroupMemberDao.updateMember(groupMember.getUserId(),groupMember.getGroupId(), GroupMemberRank.MEMBER, false);
        }

        webConnection.send("OK");
    }

    public static void confirmGiveRights(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int targetIds = webConnection.post().getArray("targetIds").size();

        var template = webConnection.template("groups/member/confirm_give_rights");
        template.set("targetIds", targetIds);
        template.render();
    }

    public static void giveRights(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int groupId = webConnection.post().getInt("groupId");
        int userId = webConnection.session().getInt("user.id");

        Group group = GroupDao.getGroup(groupId);

        if (group == null || group.getMember(userId).getMemberRank() != GroupMemberRank.OWNER) {
            webConnection.send("");
            return;
        }

        List<String> data = webConnection.post().getArray("targetIds");

        for (String user : data) {
            if (!StringUtils.isNumeric(user)) {
                continue;
            }

            int memberId = Integer.parseInt(user);
            GroupMember groupMember = group.getMember(memberId);

            if (groupMember == null || groupMember.getMemberRank() == GroupMemberRank.OWNER) {
                continue;
            }


            GroupMemberDao.updateMember(groupMember.getUserId(),groupMember.getGroupId(), GroupMemberRank.ADMINISTRATOR, false);
        }

        webConnection.send("OK");
    }

    public static void confirmRemove(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int targetIds = webConnection.post().getArray("targetIds").size();

        var template = webConnection.template("groups/member/confirm_remove");
        template.set("targetIds", targetIds);
        template.render();
    }

    public static void remove(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int groupId = webConnection.post().getInt("groupId");
        int userId = webConnection.session().getInt("user.id");

        Group group = GroupDao.getGroup(groupId);

        if (group == null || !(group.getMember(userId).getMemberRank() == GroupMemberRank.OWNER || group.getMember(userId).getMemberRank() == GroupMemberRank.ADMINISTRATOR)) {
            webConnection.send("");
            return;
        }

        List<String> data = webConnection.post().getArray("targetIds");

        for (String user : data) {
            if (!StringUtils.isNumeric(user)) {
                continue;
            }

            int memberId = Integer.parseInt(user);
            GroupMember groupMember = group.getMember(memberId);

            if (groupMember == null || groupMember.getMemberRank() == GroupMemberRank.OWNER || groupMember.getMemberRank() == GroupMemberRank.ADMINISTRATOR) {
                continue;
            }

            GroupMemberDao.deleteMember(groupMember.getUserId(), groupMember.getGroupId());

            if (groupMember.getUser().getFavouriteGroupId() == group.getId()) {
                PlayerDao.saveFavouriteGroup(groupMember.getUserId(), 0);

                if (groupMember.getUser().isOnline()) {
                    RconUtil.sendCommand(RconHeader.REFRESH_GROUP_PERMS, new HashMap<>() {{
                        put("userId", String.valueOf(userId));
                    }});
                }
            }
        }

        webConnection.send("OK");
    }

    public static void confirmAccept(WebConnection webConnection) {
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

        var template = webConnection.template("groups/member/confirm_accept");
        template.set("groupName", groupName);
        template.render();
    }

    public static void accept(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int groupId = webConnection.post().getInt("groupId");
        int userId = webConnection.session().getInt("user.id");

        Group group = GroupDao.getGroup(groupId);

        if (group == null || !(group.getMember(userId).getMemberRank() == GroupMemberRank.OWNER || group.getMember(userId).getMemberRank() == GroupMemberRank.ADMINISTRATOR)) {
            webConnection.send("");
            return;
        }

        List<String> data = webConnection.post().getArray("targetIds");

        for (String user : data) {
            if (!StringUtils.isNumeric(user)) {
                continue;
            }

            int memberId = Integer.parseInt(user);
            GroupMember groupMember = group.getPendingMember(memberId);

            if (groupMember == null || groupMember.getMemberRank() == GroupMemberRank.OWNER || groupMember.getMemberRank() == GroupMemberRank.ADMINISTRATOR) {
                continue;
            }

            GroupMemberDao.updateMember(groupMember.getUserId(), groupMember.getGroupId(), GroupMemberRank.MEMBER, false);
        }

        webConnection.send("OK");
    }

    public static void confirmDecline(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int targetIds = webConnection.post().getString("targetIds").split(",").length;

        var template = webConnection.template("groups/member/confirm_decline");
        template.set("targetIds", targetIds);
        template.render();
    }

    public static void decline(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int groupId = webConnection.post().getInt("groupId");
        int userId = webConnection.session().getInt("user.id");

        Group group = GroupDao.getGroup(groupId);

        if (group == null || !(group.getMember(userId).getMemberRank() == GroupMemberRank.OWNER || group.getMember(userId).getMemberRank() == GroupMemberRank.ADMINISTRATOR)) {
            webConnection.send("");
            return;
        }

        List<String> data = webConnection.post().getArray("targetIds");

        for (String user : data) {
            if (!StringUtils.isNumeric(user)) {
                continue;
            }

            int memberId = Integer.parseInt(user);
            GroupMember groupMember = group.getPendingMember(memberId);

            if (groupMember == null || groupMember.getMemberRank() == GroupMemberRank.OWNER || groupMember.getMemberRank() == GroupMemberRank.ADMINISTRATOR) {
                continue;
            }

            GroupMemberDao.deleteMember(groupMember.getUserId(), groupMember.getGroupId());
        }

        webConnection.send("OK");
    }

}
