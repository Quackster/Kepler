package org.alexdev.kepler.game.groups;

import org.alexdev.kepler.dao.mysql.GroupMemberDao;

import java.util.List;

public class Group {
    private int id;
    private String name;
    private String description;
    private int ownerId;
    private String badge;

    public Group(int id, String name, String description, int ownerId, String badge) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.badge = badge;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getBadge() {
        return badge.replaceAll("[^a-zA-Z0-9]", "");
    }

    public int getMemberCount(boolean isPending) {
        return GroupMemberDao.countMembers(this.id, isPending);
    }
}
