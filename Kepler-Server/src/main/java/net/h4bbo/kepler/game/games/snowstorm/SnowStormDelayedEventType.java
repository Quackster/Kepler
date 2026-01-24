package net.h4bbo.kepler.game.games.snowstorm;

import java.util.HashMap;
import java.util.Map;

/**
 * Types of delayed events that can be queued after collisions.
 */
public enum SnowStormDelayedEventType {
    HIT(0),
    STUN(1);

    private static final Map<Integer, SnowStormDelayedEventType> BY_ID = new HashMap<>();

    static {
        for (SnowStormDelayedEventType type : values()) {
            BY_ID.put(type.id, type);
        }
    }

    private final int id;

    SnowStormDelayedEventType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static SnowStormDelayedEventType fromId(int id) {
        return BY_ID.get(id);
    }
}
