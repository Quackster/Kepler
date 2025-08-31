package org.alexdev.kepler.game.groups;

import org.alexdev.kepler.dao.mysql.GroupDao;
import org.alexdev.kepler.dao.mysql.GroupMemberDao;
import org.alexdev.kepler.game.player.PlayerRank;
import org.alexdev.kepler.game.wordfilter.WordfilterManager;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.util.List;

public class Group {
    private int id;
    private String name;
    private String description;
    private int ownerId;
    private int roomId;
    private String badge;
    private boolean recommended;
    private String background;
    private int views;
    private int topics;
    private int groupType;
    private GroupForumType forumType;
    private GroupPermissionType forumPermission;
    private String alias;
    private long createdDate;
    private List<GroupMember> members;
    private List<GroupMember> pendingMembers;

    public Group(int id, String name, String description, int ownerId, int roomId, String badge, boolean recommended, String background, int views, int topics, int groupType, int forumType, int forumPermission, String alias, long createdDate) {
        this.id = id;
        this.name = WordfilterManager.filterSentence(name);
        this.description = WordfilterManager.filterSentence(description);
        this.ownerId = ownerId;
        this.roomId = roomId;
        this.badge = badge;
        this.recommended = recommended;
        this.background = background;
        this.views = views;
        this.topics = topics;
        this.groupType = groupType;
        this.forumType = GroupForumType.getById(forumType);
        this.forumPermission = GroupPermissionType.getById(forumPermission);
        this.alias = alias == null ? "" : alias;
        this.createdDate = createdDate;
    }

    public static boolean hasTopicAdmin(PlayerRank rank) {
        return rank.getRankId() >= 5;
    }

    public boolean hasAdministrator(int userId) {
        var groupMember = getMember(userId);
        return groupMember != null && (groupMember.getMemberRank() == GroupMemberRank.ADMINISTRATOR || groupMember.getMemberRank() == GroupMemberRank.OWNER);
    }

    public boolean canViewForum(GroupMember groupMember) {
        if (this.forumType == GroupForumType.PUBLIC) {
            return true;
        }

        return groupMember != null;
    }

    public boolean canReplyForum(GroupMember groupMember) {
        if (this.forumType == GroupForumType.PUBLIC) {
            return true;
        }

        return groupMember != null;
    }

    public boolean canForumPost(GroupMember groupMember) {
        if (this.forumPermission == GroupPermissionType.EVERYONE) {
            return true;
        }

        if (groupMember != null) {
            if (this.forumPermission == GroupPermissionType.ADMIN_ONLY) {
                return groupMember.getMemberRank() == GroupMemberRank.OWNER || groupMember.getMemberRank() == GroupMemberRank.ADMINISTRATOR;
            }

            if (this.forumPermission == GroupPermissionType.MEMBER_ONLY) {
                return true;
            }
        }

        return false;
    }

    public GroupMember getMember(int userId) {
        if (this.ownerId == userId) {
            return new GroupMember(this.ownerId, this.id, false, 3);
        }

        var member = GroupMemberDao.getMember(this.id, userId);

        if (member == null || member.isPending()) {
            return null;
        }

        return member;
    }

    public GroupMember getPendingMember(int userId) {
        if (this.ownerId == userId) {
            return new GroupMember(this.ownerId, this.id, false, 3);
        }

            var member = GroupMemberDao.getMember(this.id, userId);

            if (member.isPending()) {
                return member;
            } else {
                return null;
            }
    }

    public boolean isMember(int userId) {
        if (this.ownerId == userId) {
            return true;
        }

        var member = GroupMemberDao.getMember(this.id, userId);
        return member != null && !member.isPending();
    }

    public boolean isPendingMember(int userId) {
        if (this.ownerId == userId) {
            return false;
        }

        var member = GroupMemberDao.getMember(this.id, userId);
        return member != null && member.isPending();
    }

    public String generateClickLink() {
        String sitePath = GameConfiguration.getInstance().getString("site.path");

        if (this.alias != null && !this.alias.isBlank()) {
            return sitePath + "/groups/" + this.alias;
        } else {
            return sitePath + "/groups/" + this.id + "/id";
        }
    }

    public String getCreatedDate() {//May 5, 2019
        return DateUtil.getDate(this.createdDate, "MMM dd, YYYY");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getBadge() {
        return badge.replaceAll("[^a-zA-Z0-9]", "");
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getTopics() {
        return topics;
    }

    public void setTopics(int topics) {
        this.topics = topics;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public GroupForumType getForumType() {
        return forumType;
    }

    public void setForumType(GroupForumType forumType) {
        this.forumType = forumType;
    }

    public GroupPermissionType getForumPermission() {
        return forumPermission;
    }

    public void setForumPermission(GroupPermissionType forumPermission) {
        this.forumPermission = forumPermission;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void save() {
        GroupDao.saveGroup(this);
    }

    public void saveBackground() {
        GroupDao.saveBackground(this);
    }

    public void saveBadge() {
        GroupDao.saveBadge(this);
    }

    public int getMemberCount(boolean isPending) {
        return GroupMemberDao.countMembers(this.id, isPending);
    }
}
