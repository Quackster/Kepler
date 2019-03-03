#include "communication/messages/incoming_message.h"

#include "message_handler.h"
#include <shared.h>

#include "game/player/player.h"

// Login
#include "communication/incoming/login/INIT_CRYPTO.h"
#include "communication/incoming/login/GENERATEKEY.h"
#include "communication/incoming/login/TRY_LOGIN.h"
#include "communication/incoming/login/GDATE.h"
#include "communication/incoming/login/SSO.h"
#include "communication/incoming/login/PONG.h"

// Register
#include "communication/incoming/register/APPROVENAME.h"
#include "communication/incoming/register/APPROVE_PASSWORD.h"
#include "communication/incoming/register/APPROVEEMAIL.h"
#include "communication/incoming/register/PARENT_EMAIL_REQUIRED.h"
#include "communication/incoming/register/CHECK_AGE.h"
#include "communication/incoming/register/REGISTER.h"

// User
#include "communication/incoming/user/GET_INFO.h"
#include "communication/incoming/user/GET_CREDITS.h"
#include "communication/incoming/user/UPDATE.h"
#include "communication/incoming/user/UPDATE_ACCOUNT.h"
#include "communication/incoming/user/GET_CLUB.h"
#include "communication/incoming/user/SUBSCRIBE_CLUB.h"

// Messenger
#include "communication/incoming/messenger/MESSENGERINIT.h"
#include "communication/incoming/messenger/FINDUSER.h"
#include "communication/incoming/messenger/MESSENGER_ASSIGNPERSMSG.h"
#include "communication/incoming/messenger/MESSENGER_REQUESTBUDDY.h"
#include "communication/incoming/messenger/MESSENGER_DECLINEBUDDY.h"
#include "communication/incoming/messenger/MESSENGER_ACCEPTBUDDY.h"
#include "communication/incoming/messenger/MESSENGER_REMOVEBUDDY.h"
#include "communication/incoming/messenger/MESSENGER_GETREQUESTS.h"
#include "communication/incoming/messenger/MESSENGER_SENDMSG.h"
#include "communication/incoming/messenger/MESSENGER_GETMESSAGES.h"
#include "communication/incoming/messenger/MESSENGER_MARKREAD.h"
#include "communication/incoming/messenger/FOLLOW_FRIEND.h"
#include "communication/incoming/messenger/FRIENDLIST_UPDATE.h"

// Navigator
#include "communication/incoming/navigator/NAVIGATE.h"
#include "communication/incoming/navigator/SUSERF.h"
#include "communication/incoming/navigator/GETUSERFLATCATS.h"
#include "communication/incoming/navigator/RECOMMENDED_ROOMS.h"
#include "communication/incoming/navigator/ADD_FAVOURITE_ROOM.h"
#include "communication/incoming/navigator/REMOVE_FAVOURITE_ROOM.h"
#include "communication/incoming/navigator/GETFVRF.h"
#include "communication/incoming/navigator/SRCHF.h"

// Room
#include "communication/incoming/room/GETINTERST.h"
#include "communication/incoming/room/ROOM_DIRECTORY.h"
#include "communication/incoming/room/TRYFLAT.h"
#include "communication/incoming/room/GOTOFLAT.h"
#include "communication/incoming/room/GETROOMAD.h"
#include "communication/incoming/room/G_HMAP.h"
#include "communication/incoming/room/G_OBJS.h"
#include "communication/incoming/room/G_ITEMS.h"
#include "communication/incoming/room/G_STAT.h"
#include "communication/incoming/room/G_USRS.h"
#include "communication/incoming/room/GET_FURNI_REVISIONS.h"
#include "communication/incoming/room/LETUSERIN.h"
#include "communication/incoming/room/RATEFLAT.h"

// Pool
#include "communication/incoming/room/pool/SWIMSUIT.h"
#include "communication/incoming/room/pool/SPLASHPOSITION.h"
#include "communication/incoming/room/pool/DIVE.h"
#include "communication/incoming/room/pool/SIGN.h"
#include "communication/incoming/room/pool/BTCKS.h"

// Room user
#include "communication/incoming/room/user/QUIT.h"
#include "communication/incoming/room/user/WALK.h"
#include "communication/incoming/room/user/CHAT.h"
#include "communication/incoming/room/user/SHOUT.h"
#include "communication/incoming/room/user/WAVE.h"
#include "communication/incoming/room/user/LOOKTO.h"
#include "communication/incoming/room/user/CARRYDRINK.h"
#include "communication/incoming/room/user/USER_START_TYPING.h"
#include "communication/incoming/room/user/USER_CANCEL_TYPING.h"
#include "communication/incoming/room/user/DANCE.h"
#include "communication/incoming/room/user/STOP.h"

// Room settings
#include "communication/incoming/room/settings/CREATEFLAT.h"
#include "communication/incoming/room/settings/SETFLATINFO.h"
#include "communication/incoming/room/settings/GETFLATCAT.h"
#include "communication/incoming/room/settings/GETFLATINFO.h"
#include "communication/incoming/room/settings/SETFLATCAT.h"
#include "communication/incoming/room/settings/UPDATEFLAT.h"
#include "communication/incoming/room/settings/DELETEFLAT.h"

// Room items
#include "communication/incoming/room/items/PLACESTUFF.h"
#include "communication/incoming/room/items/ADDSTRIPITEM.h"
#include "communication/incoming/room/items/MOVESTUFF.h"
#include "communication/incoming/room/items/FLATPROPBYITEM.h"
#include "communication/incoming/room/items/SETSTUFFDATA.h"
#include "communication/incoming/room/items/REMOVESTUFF.h"
#include "communication/incoming/room/items/REMOVEITEM.h"
#include "communication/incoming/room/items/CONVERT_FURNI_TO_CREDITS.h"
#include "communication/incoming/room/items/G_IDATA.h"
#include "communication/incoming/room/items/SETITEMDATA.h"

// Moderation
#include "communication/incoming/room/moderation/REMOVERIGHTS.h"
#include "communication/incoming/room/moderation/ASSIGNRIGHTS.h"

// Badges
#include "communication/incoming/room/badges/GETAVAILABLEBADGES.h"
#include "communication/incoming/room/badges/SETBADGE.h"

// Catalogue
#include "communication/incoming/catalogue/GCIX.h"
#include "communication/incoming/catalogue/GCAP.h"
#include "communication/incoming/catalogue/GRPC.h"
#include "communication/incoming/catalogue/GET_ALIAS_LIST.h"

// Inventory
#include "communication/incoming/inventory/GETSTRIP.h"

// Trax
#include "communication/incoming/room/trax/GET_SONG_LIST.h"

// Trade
#include "communication/incoming/room/trade/TRADE_OPEN.h"
#include "communication/incoming/room/trade/TRADE_CLOSE.h"
#include "communication/incoming/room/trade/TRADE_ADDITEM.h"
#include "communication/incoming/room/trade/TRADE_UNACCEPT.h"
#include "communication/incoming/room/trade/TRADE_ACCEPT.h"

// Only allow these headers to be processed if the entity is not logged in.
int packet_whitelist[] = { 206, 202, 4, 49, 42, 203, 197, 146, 46, 43, 204, 196 };

/**
 * Assigns all header handlers to this array
 */
void message_handler_init() {
    // Login
    message_requests[206] = INIT_CRYPTO;
    message_requests[202] = GENERATEKEY;
    message_requests[4] = TRY_LOGIN;
    message_requests[49] = GDATE;
    message_requests[196] = PONG;

    if (configuration_get_bool("sso.tickets.enabled")) {
        message_requests[204] = SSO;
    }

    // Register
    message_requests[42] = APPROVENAME;
    message_requests[203] = APPROVE_PASSWORD;
    message_requests[197] = APPROVEEMAIL;
    message_requests[146] = PARENT_EMAIL_REQUIRED;
    message_requests[46] = CHECK_AGE;
    message_requests[43] = REGISTER;

    // User
    message_requests[7] = GET_INFO;
    message_requests[8] = GET_CREDITS;
    message_requests[44] = UPDATE;
    message_requests[149] = UPDATE_ACCOUNT;

    // Club
    message_requests[26] = GET_CLUB;
    message_requests[190] = SUBSCRIBE_CLUB;

    // Messenger
    message_requests[12] = MESSENGERINIT;
    message_requests[41] = FINDUSER;
    message_requests[40] = MESSENGER_REMOVEBUDDY;
    message_requests[36] = MESSENGER_ASSIGNPERSMSG;
    message_requests[39] = MESSENGER_REQUESTBUDDY;
    message_requests[38] = MESSENGER_DECLINEBUDDY;
    message_requests[37] = MESSENGER_ACCEPTBUDDY;
    message_requests[233] = MESSENGER_GETREQUESTS;
    message_requests[33] = MESSENGER_SENDMSG;
    message_requests[191] = MESSENGER_GETMESSAGES;
    message_requests[32] = MESSENGER_MARKREAD;
    message_requests[262] = FOLLOW_FRIEND;
    message_requests[15] = FRIENDLIST_UPDATE;

    // Navigator
    message_requests[150] = NAVIGATE;
    message_requests[16] = SUSERF;
    message_requests[151] = GETUSERFLATCATS;
    message_requests[264] = RECOMMENDED_ROOMS;
    message_requests[19] = ADD_FAVOURITE_ROOM;
    message_requests[20] = REMOVE_FAVOURITE_ROOM;
    message_requests[18] = GETFVRF;
    message_requests[17] = SRCHF;

    // Room
    message_requests[182] = GETINTERST;
    message_requests[2] = ROOM_DIRECTORY;
    message_requests[57] = TRYFLAT; // @y1052/123
    message_requests[59] = GOTOFLAT;
    message_requests[126] = GETROOMAD;
    message_requests[60] = G_HMAP;
    message_requests[62] = G_OBJS;
    message_requests[64] = G_STAT;
    message_requests[63] = G_ITEMS;
    message_requests[61] = G_USRS;
    message_requests[213] = GET_FURNI_REVISIONS;
    message_requests[98] = LETUSERIN;
    message_requests[261] = RATEFLAT;

    // Pool
    message_requests[116] = SWIMSUIT;
    message_requests[106] = DIVE;
    message_requests[107] = SPLASHPOSITION;
    message_requests[104] = SIGN;
    message_requests[105] = BTCKS;

    // Room user
    message_requests[53] = QUIT;
    message_requests[75] = WALK;
    message_requests[52] = CHAT;
    message_requests[55] = SHOUT;
    message_requests[94] = WAVE;
    message_requests[79] = LOOKTO;
    message_requests[80] = CARRYDRINK;
    message_requests[317] = USER_START_TYPING;
    message_requests[318] = USER_CANCEL_TYPING;
    message_requests[93] = DANCE;
    message_requests[88] = STOP;

    // Room settings
    message_requests[21] = GETFLATINFO;
    message_requests[29] = CREATEFLAT;
    message_requests[25] = SETFLATINFO;
    message_requests[24] = UPDATEFLAT;
    message_requests[152] = GETFLATCAT;
    message_requests[153] = SETFLATCAT;
    message_requests[23] = DELETEFLAT;

    // Room items
    message_requests[90] = PLACESTUFF;
    message_requests[73] = MOVESTUFF;
    message_requests[67] = ADDSTRIPITEM;
    message_requests[99] = REMOVESTUFF;
    message_requests[85] = REMOVEITEM;
    message_requests[74] = SETSTUFFDATA;
    message_requests[183] = CONVERT_FURNI_TO_CREDITS;
    message_requests[83] = G_IDATA;
    message_requests[84] = SETITEMDATA;

    // Moderation
    message_requests[96] = ASSIGNRIGHTS;
    message_requests[97] = REMOVERIGHTS;

    // Badges
    message_requests[157] = GETAVAILABLEBADGES;
    message_requests[158] = SETBADGE;

    // Catalogue
    message_requests[101] = GCIX;
    message_requests[102] = GCAP;
    message_requests[215] = GET_ALIAS_LIST;
    message_requests[100] = GRPC;

    // Inventory
    message_requests[65] = GETSTRIP;
    message_requests[66] = FLATPROPBYITEM;

    // Trax
    message_requests[244] = GET_SONG_LIST;

    // Trade
    message_requests[71] = TRADE_OPEN;
    message_requests[72] = TRADE_ADDITEM;
    message_requests[70] = TRADE_CLOSE;
    message_requests[68] = TRADE_UNACCEPT;
    message_requests[69] = TRADE_ACCEPT;
}

/**
 * Retrieves the handler by header id
 * @param im the incoming message struct
 * @param player the player struct
 */
void message_handler_invoke(incoming_message *im, entity *player) {
    if (configuration_get_bool("debug")) {
        char *preview = replace_unreadable_characters(im->data);
        log_debug("Client [%s] incoming data: %i / %s", player->ip_address, im->header_id, preview);
        free(preview);
    }

    // Stop the server from crashing
    if (im->header_id > 9999 || im->header_id < 0) {
        return;
    }

    // Don't process any headers it can't find
    if (message_requests[im->header_id] == NULL) {
        return;
    }

    mh_request handle = message_requests[im->header_id];

    if (player->logged_in) {
        handle(player, im);
    } else {

        // If the user isn't logged in, we only process whitelisted headers.
        for (int i = 0; i < (sizeof(packet_whitelist) / sizeof(packet_whitelist[0])); i++) {
            int header_id = packet_whitelist[i];

            if (header_id == im->header_id) {
                handle(player, im);
            }
        }
    }
}