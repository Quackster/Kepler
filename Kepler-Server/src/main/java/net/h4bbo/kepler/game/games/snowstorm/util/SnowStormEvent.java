package net.h4bbo.kepler.game.games.snowstorm.util;

public enum SnowStormEvent {
    WALK(0),
    CREATE_SNOWBALL(3),
    THROW_SNOWBALL_AT_LOCATION(2),
    THROW_SNOWBALL_AT_PERSON(1);

    private final int eventId;

    SnowStormEvent(int eventId) {
        this.eventId = eventId;
    }

    public static SnowStormEvent getEvent(int eventId) {
        for (var event : values())
            if (event.eventId == eventId)
                return event;

        return null;
    }

    public int getEventId() {
        return eventId;
    }
}
