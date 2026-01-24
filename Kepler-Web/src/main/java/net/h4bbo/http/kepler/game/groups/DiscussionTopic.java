package net.h4bbo.http.kepler.game.groups;

import net.h4bbo.kepler.game.groups.*;
import net.h4bbo.kepler.game.wordfilter.WordfilterManager;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiscussionTopic {
    private final int id;
    private final String creatorName;
    private final int creatorId;
    private final String lastReplyName;
    private final int replyCount;
    private final int groupId;
    private String topicTitle;
    private boolean isOpen;
    private boolean isStickied;
    private int views;
    private final long createdAt;
    private final long lastMessageAt;
    private boolean isNew;

    public DiscussionTopic(int id, int groupId, String topicTitle, int replyCount, boolean isOpen, boolean isStickied, int views, Time createdAt, Time lastMessageAt, int creatorId, String creatorName, String lastReplyName, boolean hasRead) {
        this.id = id;
        this.groupId = groupId;
        this.topicTitle = topicTitle;
        this.replyCount = replyCount;
        this.isOpen = isOpen;
        this.isStickied = isStickied;
        this.views = views;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.lastReplyName = lastReplyName;
        this.createdAt = createdAt.getTime() / 1000L;
        this.lastMessageAt = lastMessageAt.getTime() / 1000L;
        this.isNew = (!hasRead);
    }

    public boolean canPostReply(Group group, GroupMember member) {
        if (!this.isOpen) {
            return false;
        }

        if (group.getForumType() == GroupForumType.PRIVATE ||
                group.getForumPermission() == GroupPermissionType.MEMBER_ONLY ||
                group.getForumPermission() == GroupPermissionType.ADMIN_ONLY) {

            if (member == null) {
                return false;
            }

            if (group.getForumPermission() == GroupPermissionType.ADMIN_ONLY) {
                return member.getMemberRank() == GroupMemberRank.ADMINISTRATOR || member.getMemberRank() == GroupMemberRank.OWNER;
            }
        }


        return true;
    }

    public List<Integer> getRecentPages() {
        List<Integer> pageList = new ArrayList<>();

        int limit = GameConfiguration.getInstance().getInteger("discussions.replies.per.page");

        if (this.replyCount > limit) {
           for (int i = 0; i < 2 + 1; i++) {
                int newNumber = this.getReplyPages() - i;

                if (newNumber > 1) {
                    pageList.add(newNumber);
                }
            }
        }

        Collections.sort(pageList);
        return pageList;
    }

    public int getId() {
        return id;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getLastReplyName() {
        return lastReplyName;
    }

    public String getTopicTitle() {
        return WordfilterManager.filterSentence(topicTitle);
    }

    public int getReplyCount() {
        return replyCount;
    }

    public int getReplyPages() {
        int limit = GameConfiguration.getInstance().getInteger("discussions.replies.per.page");
        return (this.replyCount > 0 ? (int) Math.ceil((double)replyCount / (double)limit) : 1);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isStickied() {
        return isStickied;
    }

    public int getViews() {
        return views;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void setStickied(boolean stickied) {
        isStickied = stickied;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getLastMessageAt() {
        return lastMessageAt;
    }

    public String getCreatedDate(String dateFormat) {
        return DateUtil.getDate(this.createdAt, dateFormat).replace("am", "AM").replace("pm","PM").replace(".", "");
    }

    public String getLastMessage(String dateFormat) {
        return DateUtil.getDate(this.lastMessageAt, dateFormat).replace("am", "AM").replace("pm","PM").replace(".", "");
    }

    public boolean isNew() {
        return isNew;
    }
}
