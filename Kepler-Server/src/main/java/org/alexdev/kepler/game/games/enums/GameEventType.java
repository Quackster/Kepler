package org.alexdev.kepler.game.games.enums;

public enum GameEventType {
    BATTLEBALL_PLAYER_EVENT(2),
    BATTLEBALL_OBJECT_SPAWN(0),
    BATTLEBALL_DESPAWN_OBJECT(1),
    BATTLEBALL_POWERUP_GET(3),
    BATTLEBALL_POWERUP_ACTIVATE(5);

    private final int eventId;

    GameEventType(int eventId) {
        this.eventId = eventId;
    }

    public int getEventId() {
        return eventId;
    }
}
