package org.alexdev.kepler.server.rcon.messages;

public enum RconHeader {
    REFRESH_LOOKS("refresh_looks"),
    HOTEL_ALERT("hotel_alert"),
    REFRESH_CLUB("refresh_club"),
    REFRESH_HAND("refresh_hand"),
    REFRESH_CREDITS("refresh_credits"),
    REFRESH_GROUP_PERMS("refresh_group_perms");

    private final String rawHeader;

    RconHeader(String rawHeader) {
        this.rawHeader = rawHeader;
    }

    public String getRawHeader() {
        return rawHeader;
    }

    public static RconHeader getByHeader(String header) {
        for (var rconHeader : values()) {
            if (rconHeader.getRawHeader().equalsIgnoreCase(header)) {
                return rconHeader;
            }
        }

        return null;
    }
}
