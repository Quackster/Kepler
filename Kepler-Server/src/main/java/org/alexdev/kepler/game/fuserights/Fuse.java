package org.alexdev.kepler.game.fuserights;

public enum Fuse {
        // Basic fuses that apply to all users
        DEFAULT("default"),
        LOGIN("fuse_login"),
        TRADE("fuse_trade"),
        BUY_CREDITS("fuse_buy_credits"),
        BUY_CREDITS_FUSE_LOGIN("fuse_buy_credits_fuse_login"),
        ROOM_QUEUE_DEFAULT("fuse_room_queue_default"),

        // Hobba fuses
        MUTE("fuse_mute"),
        KICK("fuse_kick"),
        RECEIVE_CALLS_FOR_HELP("fuse_receive_calls_for_help"),

        // Superhobba fuses
        REMOVE_PHOTOS("fuse_remove_photos"),
        REMOVE_STICKIES("fuse_remove_stickies"),

        // Moderator fuses
        MOD("fuse_mod"),
        MODERATOR_ACCESS("fuse_moderator_access"),
        CHAT_LOG("fuse_chat_log"),
        ROOM_ALERT("fuse_room_alert"),
        ROOM_KICK("fuse_room_kick"),
        IGNORE_ROOM_OWNER("fuse_ignore_room_owner"),
        ENTER_FULL_ROOMS("fuse_enter_full_rooms"),
        ENTER_LOCKED_ROOMS("fuse_enter_locked_rooms"),
        SEE_ALL_ROOMOWNERS("fuse_see_all_roomowners"), // Not sure if this is the correct fuse
        SEARCH_USERS("fuse_search_users"),
        BAN("fuse_ban"),
        SEE_CHAT_LOG_LINK("fuse_see_chat_log_link"),
        CANCEL_ROOMEVENT("fuse_cancel_roomevent"),

        // Administrator fuses
        ADMINISTRATOR_ACCESS("fuse_administrator_access"),
        ANY_ROOM_CONTROLLER("fuse_any_room_controller"),
        PICK_UP_ANY_FURNI("fuse_pick_up_any_furni"),
        SEE_FLAT_IDS("fuse_see_flat_ids"),
        CREDITS("fuse_credits"),
        PERFORMANCE("fuse_performance_panel"),
        DEBUG("fuse_debug_window"),

        // Club fuses, these fuses do not apply to any rank
        PRIORITY_ACCESS("fuse_priority_access"),
        USE_SPECIAL_ROOM_LAYOUTS("fuse_use_special_room_layouts"),
        USE_CLUB_OUTFITS("fuse_use_club_outfits"),
        USE_CLUB_OUTFITS_DEFAULT("fuse_use_club_outfits_default"),
        USE_CLUB_BADGE("fuse_use_club_badge"),
        USE_CLUB_DANCE("fuse_use_club_dance"),
        USER_LIST_COMMAND("fuse_habbo_chooser"),
        FURNI_LIST_COMMAND("fuse_furni_chooser"),
        EXTENDED_BUDDYLIST("fuse_extended_buddylist"),
        ROOM_QUEUE_CLUB("fuse_room_queue_club"),
        EDIT_CATALOGUE("fuse_catalog_editor");

        private final String fuseName;

        Fuse(String fuseName) {
            this.fuseName = fuseName;
        }

        public String getFuseName() {
                return fuseName;
        }
}