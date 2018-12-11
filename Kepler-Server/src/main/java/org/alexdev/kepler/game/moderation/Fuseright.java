package org.alexdev.kepler.game.moderation;

import org.alexdev.kepler.game.player.PlayerRank;

public enum Fuseright {
    // Basic fuses that apply to all users
    DEFAULT("default", PlayerRank.NORMAL),
    LOGIN("fuse_login", PlayerRank.NORMAL),
    TRADE("fuse_trade", PlayerRank.NORMAL),
    BUY_CREDITS("fuse_buy_credits", PlayerRank.NORMAL),
    BUY_CREDITS_FUSE_LOGIN("fuse_buy_credits_fuse_login", PlayerRank.NORMAL),
    ROOM_QUEUE_DEFAULT("fuse_room_queue_default", PlayerRank.NORMAL),

    // Hobba fuses
    MUTE("fuse_mute", PlayerRank.HOBBA),
    KICK("fuse_kick", PlayerRank.HOBBA),
    RECEIVE_CALLS_FOR_HELP("fuse_receive_calls_for_help", PlayerRank.HOBBA),

    // Superhobba fuses
    REMOVE_PHOTOS("fuse_remove_photos", PlayerRank.SUPERHOBBA),
    REMOVE_STICKIES("fuse_remove_stickies", PlayerRank.SUPERHOBBA),

    // Moderator fuses
    MOD("fuse_mod", PlayerRank.MODERATOR),
    MODERATOR_ACCESS("fuse_moderator_access", PlayerRank.MODERATOR),
    CHAT_LOG("fuse_chat_log", PlayerRank.MODERATOR),
    ROOM_ALERT("fuse_room_alert", PlayerRank.MODERATOR),
    ROOM_KICK("fuse_room_kick", PlayerRank.MODERATOR),
    IGNORE_ROOM_OWNER("fuse_ignore_room_owner", PlayerRank.MODERATOR),
    ENTER_FULL_ROOMS("fuse_enter_full_rooms", PlayerRank.MODERATOR),
    ENTER_LOCKED_ROOMS("fuse_enter_locked_rooms", PlayerRank.MODERATOR),
    SEE_ALL_ROOMOWNERS("fuse_see_all_roomowners", PlayerRank.MODERATOR), // Not sure if this is the correct fuse
    SEARCH_USERS("fuse_search_users", PlayerRank.MODERATOR),
    BAN("fuse_ban", PlayerRank.MODERATOR),
    SEE_CHAT_LOG_LINK("fuse_see_chat_log_link", PlayerRank.MODERATOR),
    CANCEL_ROOMEVENT("fuse_cancel_roomevent", PlayerRank.MODERATOR),

    // Administrator fuses
    ADMINISTRATOR_ACCESS("fuse_administrator_access", PlayerRank.ADMINISTRATOR),
    ANY_ROOM_CONTROLLER("fuse_any_room_controller", PlayerRank.ADMINISTRATOR),
    PICK_UP_ANY_FURNI("fuse_pick_up_any_furni", PlayerRank.ADMINISTRATOR),
    SEE_FLAT_IDS("fuse_see_flat_ids", PlayerRank.ADMINISTRATOR),
    CREDITS("fuse_credits", PlayerRank.ADMINISTRATOR),

    // Club fuses, these fuses do not apply to any rank
    PRIORITY_ACCESS("fuse_priority_access", true),
    USE_SPECIAL_ROOM_LAYOUTS("fuse_use_special_room_layouts", true),
    USE_CLUB_OUTFITS("fuse_use_club_outfits", true),
    USE_CLUB_OUTFITS_DEFAULT("fuse_use_club_outfits_default", true),
    USE_CLUB_BADGE("fuse_use_club_badge", true),
    USE_CLUB_DANCE("fuse_use_club_dance", true),
    USER_LIST_COMMAND("fuse_habbo_chooser", true),
    FURNI_LIST_COMMAND("fuse_furni_chooser", true),
    EXTENDED_BUDDYLIST("fuse_extended_buddylist", true),
    ROOM_QUEUE_CLUB("fuse_room_queue_club", true),

    // Housekeeping fuses
    HOUSEKEEPING_INTRA("housekeeping_intra", PlayerRank.HOBBA),
    HOUSEKEEPING_KICK("housekeeping_kick", PlayerRank.ADMINISTRATOR), // Not sure if fuse_ is required or not
    HOUSEKEEPING_ALERT("fuse_housekeeping_alert", PlayerRank.SUPERHOBBA),
    HOUSEKEEPING_DISCUSSION("housekeeping_discussion", PlayerRank.GUIDE), // Not sure if fuse_ is required or not
    HOUSEKEEPING_DISCUSSION_ADMIN("housekeeping_discussion_admin", PlayerRank.SUPERHOBBA), // Not sure if fuse_ is required or not
    HOUSEKEEPING_ADMIN("housekeeping_admin", PlayerRank.ADMINISTRATOR),
    HOUSEKEEPING_ADMIN_CREDITS("housekeeping_admin_credits", PlayerRank.ADMINISTRATOR), // Not sure if fuse_ is required or not
    HOUSEKEEPING_ADMIN_USER_DATA("housekeeping_admin_user_data", PlayerRank.ADMINISTRATOR), // Not sure if fuse_ is required or not
    HOUSEKEEPING_ADMIN_PAYMENTS("housekeeping_admin_payments", PlayerRank.ADMINISTRATOR), // Not sure if fuse_ is required or not
    HOUSEKEEPING_CAMPAIGN("housekeeping_campaign", PlayerRank.COMMUNITY_MANAGER), // Not sure if fuse_ is required or not
    HOUSEKEEPING_CAMPAIGN_ADS("housekeeping_campaign_ads", PlayerRank.COMMUNITY_MANAGER), // Not sure if fuse_ is required or not
    HOUSEKEEPING_GEORGE("housekeeping_george", PlayerRank.MODERATOR), // Not sure if fuse_ is required or not
    HOUSEKEEPING_ADMIN_CATALOG("housekeeping_admin_catalog", PlayerRank.ADMINISTRATOR), // Not sure if fuse_ is required or not
    HOUSEKEEPING_HOBBA("housekeeping_hobba", PlayerRank.GUIDE), // Not sure if fuse_ is required or not
    HOUSEKEEPING_HOBBA_NEWBIETOOLS("housekeeping_hobba_newbietools", PlayerRank.GUIDE), // Not sure if fuse_ is required or not
    HOUSEKEEPING_HOBBA_MODERATORTOOLS("housekeeping_hobba_moderatortools", PlayerRank.HOBBA), // Not sure if fuse_ is required or not
    HOUSEKEEPING_HOBBA_HOBBATOOLS("housekeeping_hobba_hobbatools", PlayerRank.HOBBA), // Not sure if fuse_ is required or not
    HOUSEKEEPING_HOBBA_ADMINTOOLS("housekeeping_hobba_admintools", PlayerRank.ADMINISTRATOR), // Not sure if fuse_ is required or not
    HOUSEKEEPING_HOBBA_SUPERTOOLS("housekeeping_hobba_supertools", PlayerRank.SUPERHOBBA),
    HOUSEKEEPING_BAN("housekeeping_ban", PlayerRank.MODERATOR), // Not sure if fuse_ is required or not
    HOUSEKEEPING_MEGABAN("housekeeping_megaban", PlayerRank.ADMINISTRATOR), // Not sure if fuse_ is required or not
    HOUSEKEEPING_SUPERBAN("housekeeping_superban", PlayerRank.ADMINISTRATOR); // Not sure if fuse_ is required or not

    private final String fuseright;
    private final PlayerRank minimumRank;
    private final boolean requiresClub;

    Fuseright(String fuseright, PlayerRank minimumRank) {
        this.fuseright = fuseright;
        this.minimumRank = minimumRank;
        this.requiresClub = false;
    }

    Fuseright(String fuseright, boolean requiresClub) {
        this.fuseright = fuseright;
        this.minimumRank = null;
        this.requiresClub = requiresClub;
    }

    public String getFuseright() {
        return fuseright;
    }

    public boolean isClubOnly() {
        return this.requiresClub;
    }

    public PlayerRank getMinimumRank() {
        return minimumRank;
    }
}
