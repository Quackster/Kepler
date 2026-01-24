package org.alexdev.kepler.game.games.enums;

public enum GameObjectType {
    BATTLEBALL_PLAYER_OBJECT(0),
    BATTLEBALL_POWER_OBJECT(1),
    BATTLEBALL_PIN_OBJECT(2),

    SNOWWAR_AVATAR_OBJECT(5),
    SNOWWAR_SNOWMACHINE_OBJECT(4),
    SNOWWAR_SNOWBALL_OBJECT(1),

    SNOWWAR_OBJECT_EVENT(0),
    SNOWWAR_AVATAR_MOVE_EVENT(2),
    SNOWWAR_AVATAR_STOP_EVENT(6),
    SNOWWAR_REMOVE_OBJECT_EVENT(1),
    SNOWWAR_THROW_EVENT(8),
    SNOWWAR_CREATE_SNOWBALL_EVENT(7),
    SNOWWAR_TARGET_THROW_EVENT(4),
    SNOWWAR_MACHINE_MOVE_SNOWBALLS_EVENT(12),
    SNOWWAR_MACHINE_ADD_SNOWBALL_EVENT(11),
    SNOWSTORM_HIT_EVENT(5),
    SNOWWAR_STUN_EVENT(9);

    private final int objectId;

    GameObjectType(int objectId) {
        this.objectId = objectId;
    }

    public int getObjectId() {
        return objectId;
    }
}
