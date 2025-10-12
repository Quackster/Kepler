package org.alexdev.kepler.game.entity;

import org.alexdev.kepler.game.infostand.InfoStand;
import org.alexdev.kepler.game.infostand.InfoStandActive;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomUserStatus;

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
    private InfoStandActive infoStand;

    private EntityState(int entityId, int instanceId, PlayerDetails details, EntityType entityType, Room room, Position position, Map<String, RoomUserStatus> statuses) {
        this.entityId = entityId;
        this.instanceId = instanceId;
        this.details = details;
        this.entityType = entityType;
        this.room = room;
        this.position = position;
        this.statuses = new ConcurrentHashMap<>(statuses);
    }

    public static EntityState createFromEntity(Entity entity) {
        final EntityState result = new EntityState(
                entity.getDetails().getId(),
                entity.getRoomUser().getInstanceId(),
                entity.getDetails(),
                entity.getType(),
                entity.getRoomUser().getRoom(),
                entity.getRoomUser().getPosition().copy(),
                entity.getRoomUser().getStatuses());

        if (entity instanceof Player player) {
            result.infoStand = player.getInfoStand().getActive();
        }

        return result;
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

    public InfoStandActive getActiveInfoStand() {
        return infoStand;
    }
}
