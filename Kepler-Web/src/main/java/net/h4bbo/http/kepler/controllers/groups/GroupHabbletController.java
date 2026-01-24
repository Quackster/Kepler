package net.h4bbo.http.kepler.controllers.groups;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.dao.mysql.*;
import net.h4bbo.kepler.game.groups.Group;
import net.h4bbo.kepler.game.groups.GroupForumType;
import net.h4bbo.kepler.game.groups.GroupPermissionType;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.server.rcon.messages.RconHeader;
import net.h4bbo.kepler.util.StringUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.dao.GroupEditDao;
import net.h4bbo.http.kepler.dao.WidgetDao;
import net.h4bbo.http.kepler.game.stickers.StickerManager;
import net.h4bbo.http.kepler.game.stickers.StickerType;
import net.h4bbo.http.kepler.util.GroupUtil;
import net.h4bbo.http.kepler.util.HtmlUtil;
import net.h4bbo.http.kepler.util.RconUtil;

import java.util.HashMap;
import java.util.stream.Collectors;

public class GroupHabbletController {
    public static void groupCreateForm(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.send("");
            return;
        }

        PlayerDetails playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));

        if (playerDetails.getCredits() < GameConfiguration.getInstance().getInteger("group.purchase.cost")) {
            var template = webConnection.template("groups/habblet/purchase_result_error");
            template.render();
        } else {
            var template = webConnection.template("groups/habblet/group_create_form");
            template.set("groupCost", GameConfiguration.getInstance().getInteger("group.purchase.cost"));
            template.render();
        }
    }

    public static void purchaseConfirmation(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.send("");
            return;
        }

        String name = HtmlUtil.removeHtmlTags(StringUtil.filterInput(webConnection.post().getString("name"), true));

        var template = webConnection.template("groups/habblet/purchase_confirmation");
        template.set("groupName", name);
        template.set("groupCost", GameConfiguration.getInstance().getInteger("group.purchase.cost"));
        template.render();
    }

    public static void purchaseAjax(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.send("");
            return;
        }

        var template = webConnection.template("groups/habblet/purchase_ajax");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        if (playerDetails.getCredits() < GameConfiguration.getInstance().getInteger("group.purchase.cost")) {
            webConnection.send("");
            return;
        }

        CurrencyDao.decreaseCredits(playerDetails, GameConfiguration.getInstance().getInteger("group.purchase.cost"));

        RconUtil.sendCommand(RconHeader.REFRESH_CREDITS, new HashMap<>() {{
            put("userId", playerDetails.getId());
        }});

        String name = HtmlUtil.removeHtmlTags(StringUtil.filterInput(webConnection.post().getString("name"), true));
        String description = HtmlUtil.removeHtmlTags(StringUtil.filterInput(webConnection.post().getString("description"), true));

        int groupId = GroupDao.addGroup(name, description, playerDetails.getId());

        // (int userId, int x, int y, int z, String skin, int stickerId, int groupId, boolean isPlaced)
        WidgetDao.purchaseWidget(0, 40, 34, 6, 1, StickerManager.getInstance().getStickerByData("guestbookwidget", StickerType.GROUP_WIDGET).getId(), "", groupId, true);
        WidgetDao.purchaseWidget(0, 433, 40, 3, 1, StickerManager.getInstance().getStickerByData("groupinfowidget", StickerType.GROUP_WIDGET).getId(), "", groupId, true);
        WidgetDao.purchaseWidget(0, 0, 0, 0, 1, StickerManager.getInstance().getStickerByData("memberwidget", StickerType.GROUP_WIDGET).getId(), "", groupId, false);
        WidgetDao.purchaseWidget(0, 0, 0, 0, 1, StickerManager.getInstance().getStickerByData("traxplayerwidget", StickerType.GROUP_WIDGET).getId(), "", groupId, false);

        template.set("groupName", name);
        template.set("groupId", groupId);
        template.set("deductedCredits", playerDetails.getCredits());
        template.render();
    }

    public static void groupSettings(WebConnection webConnection) {
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

        if (group.getOwnerId() != userId) {
            webConnection.send("");
            return;
        }

        var template = webConnection.template("groups/habblet/group_settings");
        template.set("group", group);
        template.set("selected" + group.getGroupType() + "GroupType", " checked=\"checked\"");
        template.set("selected" + group.getForumType().getId() + "ForumType", " checked=\"checked\"");
        template.set("selected" + group.getForumPermission().getId() + "ForumPermissionType", " checked=\"checked\"");
        template.set("charactersLeft", String.valueOf(255 - group.getDescription().length()));
        template.set("rooms", RoomDao.getRoomsByUserId(userId).stream().filter(room -> room.getData().getGroupId() == 0 || room.getData().getGroupId() == group.getId()).collect(Collectors.toList()));
        template.render();
    }

    public static void checkGroupUrl(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        var template = webConnection.template("groups/habblet/check_group_url");
        template.set("url", HtmlUtil.escape(webConnection.post().getString("url")));
        template.render();
    }

    public static void updateGroupSettings(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        int groupId = webConnection.post().getInt("groupId");

        Group group = GroupDao.getGroup(groupId);

        if (group == null) {
            return;
        }

        if (group.getOwnerId() != userId) {
            return;
        }

        /**

         name: Alex's group
         description: lol fuck u xdddd
         groupId: 114
         type: 1
         url:
         forumType: 0
         newTopicPermission: 0

         */

        String name = HtmlUtil.removeHtmlTags(webConnection.post().getString("name"));
        String description = HtmlUtil.removeHtmlTags(webConnection.post().getString("description"));
        String url = webConnection.post().getString("url").replaceAll("[^a-zA-Z0-9]", "");

        if (url.length() > 30) {
            url = url.substring(0, 30);
        }

        if (name.length() > 30) {
            name = name.substring(0, 30);
        }

        if (description.length() > 255) {
            description = description.substring(0, 255);
        }

        int groupType = webConnection.post().getInt("type");
        int forumType = webConnection.post().getInt("forumType");
        int forumTypePermission = webConnection.post().getInt("newTopicPermission");
        int roomId = 0;

        try {
            roomId = webConnection.post().getString("roomId").length() > 0 ? webConnection.post().getInt("roomId") : 0;
        } catch (Exception ex) {

        }
        if (groupType < 0 || groupType > 3) {
            groupType = 0;
        }

        if (roomId < 0) {
            roomId = 0;
        }

        if (forumType < 0 || forumType > 1) {
            forumType = 0;
        }

        if (forumTypePermission < 0 || forumTypePermission > 2) {
            forumTypePermission = 0;
        }

        group.setName(name);
        group.setDescription(description);

        if (group.getGroupType() != 3) {
            group.setGroupType(groupType);
        }

        group.setForumType(GroupForumType.getById(forumType));
        group.setForumPermission(GroupPermissionType.getById(forumTypePermission));

        if (group.getAlias() == null || group.getAlias().isBlank()) {
            group.setAlias(null);

            if (!url.isBlank()) {
                boolean existing = GroupDao.hasGroupByAlias(url);

                if (!existing) {
                    group.setAlias(url);
                }
            }
        }

        RoomDao.saveGroupId(group.getRoomId(), 0);

        if (roomId > 0) {
            Room room = RoomDao.getRoomById(roomId);

            if (room == null || room.getData().getOwnerId() != userId) {
                roomId = 0;
            } else {
                RoomDao.saveGroupId(roomId, groupId);
            }
        }

        group.setRoomId(roomId);
        group.save();

        GroupUtil.refreshGroup(groupId);

        var template = webConnection.template("groups/habblet/update_group_settings");
        template.set("group", group);
        template.set("message", "Editing group settings successful");
        template.render();
    }

    public static void showBadgeEditor(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        int groupId = webConnection.post().getInt("groupId");

        Group group = GroupDao.getGroup(groupId);

        if (group == null) {
            return;
        }

        if (group.getOwnerId() != userId) {
            return;
        }

        var template = webConnection.template("groups/habblet/show_badge_editor");
        template.set("group", group);
        template.render();
    }

    public static void updateGroupBadge(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        int groupId = webConnection.post().getInt("groupId");
        String badge = HtmlUtil.removeHtmlTags(webConnection.post().getString("code"));

        Group group = GroupDao.getGroup(groupId);

        if (group == null) {
            return;
        }

        if (group.getOwnerId() != userId) {
            return;
        }

        group.setBadge(badge.replaceAll("[^a-zA-Z0-9]", ""));
        group.saveBadge();

        GroupUtil.refreshGroup(groupId);
        webConnection.redirect(group.generateClickLink());
    }

    public static void confirmDeleteGroup(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        int groupId = webConnection.post().getInt("groupId");

        Group group = GroupDao.getGroup(groupId);

        if (group == null) {
            return;
        }

        if (group.getOwnerId() != userId) {
            return;
        }

        var template = webConnection.template("groups/habblet/confirm_delete_group");
        template.set("group", group);
        template.render();
    }

    public static void deleteGroup(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        int groupId = webConnection.post().getInt("groupId");

        Group group = GroupDao.getGroup(groupId);

        if (group == null) {
            return;
        }

        if (group.getOwnerId() != userId) {
            return;
        }

        GroupEditDao.deleteGroupWidgets(groupId);
        GroupEditDao.pickupUserWidgets(groupId);

        GroupMemberDao.deleteMembers(groupId);
        GroupMemberDao.resetFavourites(groupId);
        GroupDao.delete(groupId);

        RconUtil.sendCommand(RconHeader.GROUP_DELETED, new HashMap<>() {{
            put("groupId", String.valueOf(groupId));
        }});

        var template = webConnection.template("groups/habblet/delete_group");
        template.render();
    }
}
