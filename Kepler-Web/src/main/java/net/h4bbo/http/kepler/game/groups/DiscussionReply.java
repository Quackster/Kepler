package net.h4bbo.http.kepler.game.groups;

import net.h4bbo.kepler.game.wordfilter.WordfilterManager;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.http.kepler.util.BBCode;
import net.h4bbo.http.kepler.util.HtmlUtil;

import java.sql.Time;

public class DiscussionReply {
    private final int id;
    private final int userId;
    private final boolean isNew;
    private final int groupId;
    private String message;
    private final String figure;
    private final String username;
    private final boolean isOnline;
    private final String equippedBadge;
    private final String groupBadge;
    private final int forumMessages;
    private final long createdAt;
    private boolean isEdited;
    private boolean isDeleted;
    private final long modifiedAt;

    public DiscussionReply(int id, int userId, String message, String figure, String username, boolean isOnline, String equippedBadge, int groupId, String groupBadge, int forumMessages, boolean isEdited, boolean isDeleted, Time createdAt, Time modifiedAt, boolean hasRead) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.figure = figure;
        this.username = username;
        this.isOnline = isOnline;
        this.equippedBadge = equippedBadge;
        this.groupId = groupId;
        this.groupBadge = groupBadge;
        this.forumMessages = forumMessages;
        this.isEdited = isEdited;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt.getTime() / 1000L;
        this.modifiedAt = modifiedAt.getTime() / 1000L;
        this.isNew = (!hasRead);
    }

    public String getCreatedDate(String dateFormat) {
        return DateUtil.getDate(this.createdAt, dateFormat).replace("am", "AM").replace("pm","PM").replace(".", "");
    }

    public String getEditedDate(String dateFormat) {
        return DateUtil.getDate(this.modifiedAt, dateFormat).replace("am", "AM").replace("pm","PM").replace(".", "");
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getMessage() {
        return WordfilterManager.filterSentence(this.message);
    }

    public String getFormattedMessage() {
        return BBCode.format(HtmlUtil.escape(BBCode.normalise(WordfilterManager.filterSentence(this.message))), false);
    }

    public String getFigure() {
        return figure;
    }

    public String getUsername() {
        return username;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public String getEquippedBadge() {
        return equippedBadge;
    }

    public boolean hasBadge() {
        return equippedBadge != null;
    }

    public int getGroupId() {
        return groupId;
    }

    public boolean hasGroupBadge() {
        return groupBadge != null;
    }

    public String getGroupBadge() {
        return groupBadge;
    }

    public int getForumMessages() {
        return forumMessages;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public long getModifiedAt() {
        return modifiedAt;
    }

    public void setMessage(String message) {
        this.message = WordfilterManager.filterSentence(message);
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isNew() {
        return isNew;
    }
}
