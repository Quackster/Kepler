package org.alexdev.kepler.game.fuserights;

public enum Fuse {
        // Basic fuses that apply to all users
        DEFAULT("default"),
        //LOGIN("fuse_login"), // NOT USED
        //TRADE("fuse_trade"), // NOT USED
        //BUY_CREDITS("fuse_buy_credits"), // NOT USED
        //BUY_CREDITS_FUSE_LOGIN("fuse_buy_credits_fuse_login"), // NOT USED
        //ROOM_QUEUE_DEFAULT("fuse_room_queue_default"), // NOT USED

        // Hobba fuses
        //MUTE("fuse_mute"), // NOT USED
        KICK("fuse_kick"),
        RECEIVE_CALLS_FOR_HELP("fuse_receive_calls_for_help"),

        // Superhobba fuses
        REMOVE_PHOTOS("fuse_remove_photos"),
        REMOVE_STICKIES("fuse_remove_stickies"),

        // Moderator fuses
        //MOD("fuse_mod"), // NOT USED
        //MODERATOR_ACCESS("fuse_moderator_access"),// NOT USED
        //CHAT_LOG("fuse_chat_log"), // NOT USED
        ROOM_ALERT("fuse_room_alert"),
        ROOM_KICK("fuse_room_kick"),
        IGNORE_ROOM_OWNER("fuse_ignore_room_owner"),
        ENTER_FULL_ROOMS("fuse_enter_full_rooms"),
        ENTER_LOCKED_ROOMS("fuse_enter_locked_rooms"),
        SEE_ALL_ROOMOWNERS("fuse_see_all_roomowners"),
        //SEARCH_USERS("fuse_search_users"), // NOT USED
        BAN("fuse_ban"),
        SEE_CHAT_LOG_LINK("fuse_see_chat_log_link"), // USED CLIENT SIDE - NOT SERVER SIDE
        CANCEL_ROOMEVENT("fuse_cancel_roomevent"),

        // Administrator fuses
        ADMINISTRATOR_ACCESS("fuse_administrator_access"),
        ANY_ROOM_CONTROLLER("fuse_any_room_controller"),
        PICK_UP_ANY_FURNI("fuse_pick_up_any_furni"),
        //SEE_FLAT_IDS("fuse_see_flat_ids"), // USED CLIENT SIDE - NOT SERVER SIDE
        //CREDITS("fuse_credits"), // Not used
        PERFORMANCE("fuse_performance_panel"), // Client side
        DEBUG("fuse_debug_window"), // Grants access to dev commands like :reload

        // Club fuses, these fuses do not apply to any rank
        //PRIORITY_ACCESS("fuse_priority_access"), // Not used
        USE_SPECIAL_ROOM_LAYOUTS("fuse_use_special_room_layouts"),
        //USE_CLUB_OUTFITS("fuse_use_club_outfits"), // Not used
        //USE_CLUB_OUTFITS_DEFAULT("fuse_use_club_outfits_default"), // Not used
        //USE_CLUB_BADGE("fuse_use_club_badge"), // Not used
        USE_CLUB_DANCE("fuse_use_club_dance"),
        USER_LIST_COMMAND("fuse_habbo_chooser"),
        FURNI_LIST_COMMAND("fuse_furni_chooser"),
        EXTENDED_BUDDYLIST("fuse_extended_buddylist"),
        //ROOM_QUEUE_CLUB("fuse_room_queue_club"), // Not used
        //EDIT_CATALOGUE("fuse_catalog_editor"), // Not used
        GIVE_CREDITS("fuse_give_credits");

        private final String fuseName;

        Fuse(String fuseName) {
            this.fuseName = fuseName;
        }

        public String getFuseName() {
                return fuseName;
        }
}