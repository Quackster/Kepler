package org.alexdev.kepler.game.games.snowstorm.util;

import java.util.HashMap;
import java.util.Map;

public enum SnowStormEvent {
    WALK(0),
    CREATE_SNOWBALL(3),
    THROW_SNOWBALL_AT_LOCATION(2),
    THROW_SNOWBALL_AT_PERSON(1);

    private static final Map<Integer, SnowStormEvent> BY_ID = new HashMap<>();

    static {
        for (SnowStormEvent event : values()) {
            BY_ID.put(event.eventId, event);
        }
    }

    private final int eventId;

    SnowStormEvent(int eventId) {
        this.eventId = eventId;
    }

    public static SnowStormEvent getEvent(int eventId) {
        return BY_ID.get(eventId);
    }

    public int getEventId() {
        return eventId;
    }
}
