package org.alexdev.kepler.game.games.enums;

public enum GameObjectType {
    BATTLEBALL_PLAYER_OBJECT(0),
    BATTLEBALL_POWER_OBJECT(1),
    BATTLEBALL_PIN_OBJECT(2),

    SNOWWAR_AVATAR_OBJECT(5),
    SNOWWAR_PLAYER_OBJECT(0),

    SNOWWAR_OBJECT_EVENT(0);

    private final int objectId;

    GameObjectType(int objectId) {
        this.objectId = objectId;
    }

    public int getObjectId() {
        return objectId;
    }
}
