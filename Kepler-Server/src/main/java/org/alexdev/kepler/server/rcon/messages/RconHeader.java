package org.alexdev.kepler.server.rcon.messages;

public enum RconHeader {
    REFRESH_LOOKS("refresh_looks"),
    HOTEL_ALERT("hotel_alert"),
    REFRESH_CLUB("refresh_club"),
    REFRESH_TAGS("refresh_tags"),
    REFRESH_HAND("refresh_hand"),
    REFRESH_CREDITS("refresh_credits"),
    FRIEND_REQUEST("friendrequest"),
    REFRESH_MESSENGER_CATEGORIES("refreshmessengercategories"),
    REFRESH_TRADE_SETTING("refreshtrade"),
    GROUP_DELETED("groupdeleted"),
    REFRESH_GROUP("refreshgroup"),
    REFRESH_GROUP_PERMS("refreshgroupperms"),
    REFRESH_ADS("refreshads"),
    INFOBUS_POLL("infobuspoll"),
    INFOBUS_DOOR_STATUS("infobusdoorstatus"),
    INFOBUS_END_EVENT("infobusendevent"),
    REFRESH_CATALOGUE_FRONTPAGE("refreshcataloguefrontpage"),
    CLEAR_PHOTO("clearphoto"),
    DISCONNECT_USER("disconnect"),
    REFRESH_STATISTICS("refreshstats"),
    REFRESH_ROOM_BADGES("refreshroombadges");

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
