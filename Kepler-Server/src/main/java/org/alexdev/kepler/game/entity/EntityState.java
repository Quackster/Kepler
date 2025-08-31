package org.alexdev.kepler.game.entity;

import org.alexdev.kepler.game.badges.Badge;
import org.alexdev.kepler.game.groups.GroupMember;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomUserStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityState {
    private int entityId;
    private int instanceId;
    private PlayerDetails details;
    private EntityType entityType;
    private Room room;
    private Position position;
    private Map<String, RoomUserStatus> statuses;
    private List<Badge> badgeList;
    private GroupMember groupMember;

    public EntityState(int entityId, int instanceId, PlayerDetails details, EntityType entityType, Room room, Position position, Map<String, RoomUserStatus> statuses) {
        this.entityId = entityId;
        this.instanceId = instanceId;
        this.details = details;
        this.entityType = entityType;
        this.room = room;
        this.position = position;
        this.statuses = new ConcurrentHashMap<>(statuses);
        this.badgeList = new ArrayList<>();

        if (details.getFavouriteGroupId() > 0 &&
                details.getGroupMember() != null) {
            this.groupMember = details.getGroupMember();
        }
    }

    public int getInstanceId() {
        return instanceId;
    }

    public Position getPosition() {
        return position;
    }

    public Map<String, RoomUserStatus> getStatuses() {
        return statuses;
    }

    public int getEntityId() {
        return entityId;
    }

    public PlayerDetails getDetails() {
        return details;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Room getRoom() {
        return room;
    }

    public List<Badge> getBadges() {
        return badgeList;
    }

    public GroupMember getGroupMember() {
        return groupMember;
    }
}
