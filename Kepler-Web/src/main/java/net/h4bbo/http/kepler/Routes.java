package net.h4bbo.http.kepler;

import org.alexdev.duckhttpd.routes.RouteManager;
import net.h4bbo.http.kepler.controllers.BaseController;
import net.h4bbo.http.kepler.controllers.api.*;
import net.h4bbo.http.kepler.controllers.groups.*;
import net.h4bbo.http.kepler.controllers.groups.discussions.DiscussionActionsController;
import net.h4bbo.http.kepler.controllers.groups.discussions.DiscussionController;
import net.h4bbo.http.kepler.controllers.groups.discussions.DiscussionPreviewController;
import net.h4bbo.http.kepler.controllers.habblet.*;
import net.h4bbo.http.kepler.controllers.homes.HomesController;
import net.h4bbo.http.kepler.controllers.homes.NoteEditorController;
import net.h4bbo.http.kepler.controllers.homes.WidgetController;
import net.h4bbo.http.kepler.controllers.homes.store.StoreController;
import net.h4bbo.http.kepler.controllers.homes.widgets.*;
import net.h4bbo.http.kepler.controllers.housekeeping.*;
import net.h4bbo.http.kepler.controllers.site.*;

public class Routes {
    public static String HOUSEKEEPING_PATH = "allseeingeye/hk";
    
    public static void register() {
        RouteManager.addRoute(new String[] { "/", "/index", "/home"}, HomepageController::homepage);
        RouteManager.addRoute("/maintenance", HomepageController::maintenance);

        RouteManager.addRoute("", new BaseController());

        // Site
        RouteManager.addRoute("/me", AccountController::me);
        RouteManager.addRoute("/welcome", AccountController::welcome);

        // News
        RouteManager.addRoute("/articles", NewsController::articles);
        RouteManager.addRoute("/articles/archive", NewsController::articles);
        RouteManager.addRoute("/articles/category/*", NewsController::articles);
        RouteManager.addRoute("/articles/*-*", NewsController::articles);

        // Events
        RouteManager.addRoute("/community/events", NewsController::events);
        RouteManager.addRoute("/community/events/archive", NewsController::events);
        RouteManager.addRoute("/community/events/category/*", NewsController::events);
        RouteManager.addRoute("/community/events/*-*", NewsController::events);
        //RouteManager.addRoute("/events/steampunk", CustomEventsController::steampunk);

        // Fansites
        RouteManager.addRoute("/community/fansites", NewsController::fansites);
        RouteManager.addRoute("/community/fansites/archive", NewsController::fansites);
        RouteManager.addRoute("/community/fansites/category/*", NewsController::events);
        RouteManager.addRoute("/community/fansites/*-*", NewsController::fansites);

        // Site
        RouteManager.addRoute("/community", CommunityController::community);
        RouteManager.addRoute("/games", GamesController::games);
        RouteManager.addRoute("/games/score_all_time", GamesController::games_all_time);
        RouteManager.addRoute("/habblet/personalhighscores", GamesController::personalhighscores);
        RouteManager.addRoute("/credits", CreditsController::credits);
        RouteManager.addRoute("/credits/history", CreditsController::transactions);
        RouteManager.addRoute("/credits/pixels", SiteController::pixels);
        RouteManager.addRoute("/credits/club", ClubController::club);
        RouteManager.addRoute("/credits/collectables", CollectablesController::collectables);
        RouteManager.addRoute("/credits/club/tryout", ClubController::clubTryout);
        RouteManager.addRoute("/tag", TagController::tag);
        RouteManager.addRoute("/tag/*", TagController::search);
        //RouteManager.addRoute("/help/install_shockwave", SiteController::install_shockwave);
        //RouteManager.addRoute("/help/shockwave_app", SiteController::shockwave_app);
        RouteManager.addRoute("/help/*", FaqController::faq);

        // Client
        RouteManager.addRoute("/components/updateHabboCount", ClientController::updateHabboCount);
        RouteManager.addRoute("/client", ClientController::client);
        RouteManager.addRoute("/clientlog/update", ClientController::blank);
        RouteManager.addRoute("/cacheCheck", ClientController::blank);
        RouteManager.addRoute("/shockwave_client", ClientController::shockwaveclient);
        RouteManager.addRoute("/flash_client", ClientController::flashClient);
        //RouteManager.addRoute("/beta_client", ClientController::betaClient); // R34 client: deprecated
        RouteManager.addRoute("/client_popup/install_shockwave", ClientController::clientInstallShockwave);
        RouteManager.addRoute("/client_error", ClientController::client_error);
        RouteManager.addRoute("/client_connection_failed", ClientController::client_connection_failed);


        // Account
        RouteManager.addRoute("/account/banned", AccountController::banned);
        RouteManager.addRoute("/account/logout", AccountController::logout);
        RouteManager.addRoute("/account/login", AccountController::login_popup);
        RouteManager.addRoute("/account/password/forgot", RecoveryController::forgot);
        RouteManager.addRoute("/account/password/recovery", RecoveryController::recovery);
        RouteManager.addRoute("/account/activate", RecoveryController::activate);
        RouteManager.addRoute("/login_popup", AccountController::login_popup);
        RouteManager.addRoute("/account/submit", AccountController::submit);
        RouteManager.addRoute("/security_check", AccountController::securityCheck);
        RouteManager.addRoute("/account/reauthenticate", AccountController::reauthenticate);

        // Profile
        RouteManager.addRoute("/profile", ProfileController::profile);
        //RouteManager.addRoute("/profile/flash", ProfileController::profile_flash);
        //RouteManager.addRoute("/profile/verify", ProfileController::verify);
        //RouteManager.addRoute("/profile/send_email", ProfileController::send_email);

        RouteManager.addRoute("/profile/wardrobeStore", ProfileController::wardrobeStore);
        RouteManager.addRoute("/profile/passwordupdate", ProfileController::passwordupdate);
        RouteManager.addRoute("/profile/emailupdate", ProfileController::emailupdate);
        RouteManager.addRoute("/profile/characterupdate", ProfileController::characterupdate);
        RouteManager.addRoute("/profile/profile.action", ProfileController::action);
        RouteManager.addRoute("/profile/profileupdate", ProfileController::profileupdate);
        // RouteManager.addRoute("/profile/securitysettingupdate", ProfileController::securitysettingupdate);
        RouteManager.addRoute("/club", ProfileController::club);

        RouteManager.addRoute("/friendmanagement/ajax/editCategory", FriendManagementController::editCategory);
        RouteManager.addRoute("/friendmanagement/ajax/createcategory", FriendManagementController::createcategory);
        RouteManager.addRoute("/friendmanagement/ajax/deletecategory", FriendManagementController::deletecategory);
        RouteManager.addRoute("/friendmanagement/ajax/viewcategory", FriendManagementController::viewCategory);
        RouteManager.addRoute("/friendmanagement/ajax/updatecategoryoptions", FriendManagementController::updateCategoryOptions);
        RouteManager.addRoute("/friendmanagement/ajax/movefriends", FriendManagementController::movefriends);
        RouteManager.addRoute("/friendmanagement/ajax/deletefriends", FriendManagementController::deletefriends);

        // Register
        RouteManager.addRoute("/register", RegisterController::register);
        RouteManager.addRoute("/register/cancel", RegisterController::registerCancelled);
        RouteManager.addRoute("/captcha.jpg", RegisterController::captcha);

        // Habblets
        RouteManager.addRoute("/habblet/ajax/namecheck", NameCheckController::namecheck);
        RouteManager.addRoute("/habblet/ajax/updatemotto", UpdateMottoController::updatemotto);
        RouteManager.addRoute("/habblet/ajax/roomselectionCreate", RoomSelectionController::create);
        RouteManager.addRoute("/habblet/ajax/roomselectionConfirm", RoomSelectionController::confirm);
        RouteManager.addRoute("/habblet/ajax/roomselectionHide", RoomSelectionController::hide);
        RouteManager.addRoute("/components/roomNavigation", NavigationComponent::navigation);
        RouteManager.addRoute("/habblet/proxy", ProxyHabblet::moreInfo);
        RouteManager.addRoute("/habboclub/habboclub_confirm", HabboClubHabblet::confirm);
        RouteManager.addRoute("/habboclub/habboclub_subscribe", HabboClubHabblet::subscribe);
        RouteManager.addRoute("/habboclub/habboclub_reminder_remove", HabboClubHabblet::reminderRemove);
        RouteManager.addRoute("/habblet/ajax/habboclub_gift", ClubController::habboClubGift);
        RouteManager.addRoute("/habblet/ajax/habboclub_enddate", HabboClubHabblet::enddate);
        RouteManager.addRoute("/myhabbo/tag/add", TagController::add);
        RouteManager.addRoute("/myhabbo/tag/remove", TagController::remove);
        RouteManager.addRoute("/habblet/ajax/redeemvoucher", VoucherController::redeemVoucher);
        RouteManager.addRoute("/remove_all_tags", TagController::remove_all_tags);
        RouteManager.addRoute("/habblet/ajax/tagsearch", TagController::tagsearch);
        RouteManager.addRoute("/habblet/ajax/tagfight", TagController::tagfight);
        RouteManager.addRoute("/habblet/mytagslist", TagController::mytaglist);
        RouteManager.addRoute("/habblet/ajax/tagmatch", TagController::tagmatch);
        RouteManager.addRoute("/habblet/ajax/tagmatch", TagController::tagmatch);
        RouteManager.addRoute("/habblet/ajax/collectiblesConfirm", CollectablesController::confirm);
        RouteManager.addRoute("/habblet/ajax/collectiblesPurchase", CollectablesController::purchase);
        RouteManager.addRoute("/habblet/ajax/load_events", EventController::loadEvents);
        RouteManager.addRoute("/habblet/ajax/mgmgetinvitelink", InviteController::inviteLink);
        RouteManager.addRoute("/habblet/habbosearchcontent", InviteController::searchContent);
        RouteManager.addRoute("/habblet/ajax/confirmAddFriend", InviteController::confirmAddFriend);
        RouteManager.addRoute("/habblet/ajax/addFriend", InviteController::addFriend);
        RouteManager.addRoute("/myhabbo/avatarlist/avatarinfo", FriendsWidgetController::avatarinfo);
        RouteManager.addRoute("/myhabbo/friends/add", InviteController::add);
        RouteManager.addRoute("/habblet/cproxy", ProxyHabblet::minimail);
        RouteManager.addRoute("/habblet/ajax/removeFeedItem", FeedController::removeFeedItem);
        RouteManager.addRoute("/habblet/ajax/nextgift", FeedController::nextgift);
        RouteManager.addRoute("/habblet/ajax/giftqueueHide", FeedController::giftqueueHide);
        RouteManager.addRoute("/habblet/ajax/clear_hand", ProxyHabblet::clearhand);
        RouteManager.addRoute("/habblet/ajax/token_generate", ProxyHabblet::token_generate);
        RouteManager.addRoute("/habblet/ajax/preview_news_article", HousekeepingNewsController::preview_news_article);

        // Groups
        RouteManager.addRoute("/groups/*/id", GroupController::viewGroup);
        RouteManager.addRoute("/groups/*", GroupController::viewGroup);
        RouteManager.addRoute("/grouppurchase/group_create_form", GroupHabbletController::groupCreateForm);
        RouteManager.addRoute("/grouppurchase/purchase_confirmation", GroupHabbletController::purchaseConfirmation);
        RouteManager.addRoute("/grouppurchase/purchase_ajax", GroupHabbletController::purchaseAjax);
        RouteManager.addRoute("/groups/actions/startEditingSession/*", GroupController::startEditingSession);
        RouteManager.addRoute("/groups/actions/cancelEditingSession", GroupController::cancelEditingSession);
        RouteManager.addRoute("/groups/actions/group_settings", GroupHabbletController::groupSettings);
        RouteManager.addRoute("/groups/actions/saveEditingSession", GroupController::saveEditingSession);
        RouteManager.addRoute("/groups/actions/update_group_settings", GroupHabbletController::updateGroupSettings);
        RouteManager.addRoute("/groups/actions/check_group_url",  GroupHabbletController::checkGroupUrl);
        RouteManager.addRoute("/groups/actions/show_badge_editor", GroupHabbletController::showBadgeEditor);
        RouteManager.addRoute("/groups/actions/update_group_badge", GroupHabbletController::updateGroupBadge);
        RouteManager.addRoute("/groups/actions/confirm_delete_group", GroupHabbletController::confirmDeleteGroup);
        RouteManager.addRoute("/groups/actions/delete_group", GroupHabbletController::deleteGroup);
        RouteManager.addRoute("/myhabbo/tag/addgrouptag", GroupTagController::addGroupTag);
        RouteManager.addRoute("/myhabbo/tag/listgrouptags", GroupTagController::listGroupTag);
        RouteManager.addRoute("/myhabbo/tag/removegrouptag", GroupTagController::removeGroupTag);
        RouteManager.addRoute("/groups/actions/join", GroupMemberController::join);
        RouteManager.addRoute("/groups/actions/confirm_leave", GroupMemberController::confirmLeave);
        RouteManager.addRoute("/groups/actions/leave", GroupMemberController::leave);
        RouteManager.addRoute("/myhabbo/groups/memberlist", GroupMemberController::memberlist);
        RouteManager.addRoute("/myhabbo/groups/batch/confirm_revoke_rights", GroupMemberController::confirmRevokeRights);
        RouteManager.addRoute("/myhabbo/groups/batch/revoke_rights", GroupMemberController::revokeRights);
        RouteManager.addRoute("/myhabbo/groups/batch/confirm_give_rights", GroupMemberController::confirmGiveRights);
        RouteManager.addRoute("/myhabbo/groups/batch/give_rights", GroupMemberController::giveRights);
        RouteManager.addRoute("/myhabbo/groups/batch/confirm_remove", GroupMemberController::confirmRemove);
        RouteManager.addRoute("/myhabbo/groups/batch/remove", GroupMemberController::remove);
        RouteManager.addRoute("/myhabbo/groups/batch/confirm_accept", GroupMemberController::confirmAccept);
        RouteManager.addRoute("/myhabbo/groups/batch/accept", GroupMemberController::accept);
        RouteManager.addRoute("/myhabbo/groups/batch/confirm_decline", GroupMemberController::confirmDecline);
        RouteManager.addRoute("/myhabbo/groups/batch/decline", GroupMemberController::decline);
        RouteManager.addRoute("/myhabbo/avatarlist/membersearchpaging", MemberWidgetController::membersearchpaging);
        RouteManager.addRoute("/groups/actions/confirm_select_favorite", GroupFavouriteController::confirmselectfavourite);
        RouteManager.addRoute("/groups/actions/select_favorite", GroupFavouriteController::selectfavourite);
        RouteManager.addRoute("/groups/actions/confirm_deselect_favorite", GroupFavouriteController::confirmdeselectfavourite);
        RouteManager.addRoute("/groups/actions/deselect_favorite", GroupFavouriteController::deselectfavourite);

        // Group discussions
        RouteManager.addRoute("/groups/*/id/discussions/page/*", GroupDiscussionsController::viewDiscussionsPage);
        RouteManager.addRoute("/groups/*/discussions/page/*", GroupDiscussionsController::viewDiscussionsPage);
        RouteManager.addRoute("/groups/*/id/discussions", GroupDiscussionsController::viewDiscussions);
        RouteManager.addRoute("/groups/*/discussions", GroupDiscussionsController::viewDiscussions);
        RouteManager.addRoute("/groups/*/id/discussions/*/id", DiscussionController::viewDiscussion);
        RouteManager.addRoute("/groups/*/discussions/*/id", DiscussionController::viewDiscussion);
        RouteManager.addRoute("/groups/*/id/discussions/*/id/page/*", DiscussionController::viewDiscussion);
        RouteManager.addRoute("/groups/*/discussions/*/id/page/*", DiscussionController::viewDiscussion);
        RouteManager.addRoute("/discussions/actions/pingsession", DiscussionActionsController::pingsession);
        RouteManager.addRoute("/discussions/actions/newtopic", DiscussionActionsController::newtopic);
        RouteManager.addRoute("/discussions/actions/savetopic", DiscussionActionsController::savetopic);
        RouteManager.addRoute("/discussions/actions/previewtopic", DiscussionPreviewController::previewtopic);
        RouteManager.addRoute("/discussions/actions/previewpost", DiscussionPreviewController::previewpost);
        RouteManager.addRoute("/discussions/actions/opentopicsettings", DiscussionActionsController::opentopicsettings);
        RouteManager.addRoute("/discussions/actions/confirm_delete_topic", DiscussionActionsController::confirm_delete_topic);
        RouteManager.addRoute("/discussions/actions/deletetopic", DiscussionActionsController::deletetopic);
        RouteManager.addRoute("/discussions/actions/savetopicsettings", DiscussionActionsController::savetopicsettings);
        RouteManager.addRoute("/discussions/actions/updatepost", DiscussionActionsController::updatepost);
        RouteManager.addRoute("/discussions/actions/deletepost", DiscussionActionsController::deletepost);
        RouteManager.addRoute("/discussions/actions/savepost", DiscussionActionsController::savepost);

        // Store
        RouteManager.addRoute("/myhabbo/store/main", StoreController::main);
        RouteManager.addRoute("/myhabbo/store/items", StoreController::items);
        RouteManager.addRoute("/myhabbo/store/preview", StoreController::preview);
        RouteManager.addRoute("/myhabbo/store/purchase_confirm", StoreController::purchaseConfirm);
        RouteManager.addRoute("/myhabbo/store/background_warning", StoreController::backgroundWarning);
        RouteManager.addRoute("/myhabbo/store/purchase_stickers", StoreController::purchaseStickers);
        RouteManager.addRoute("/myhabbo/store/purchase_backgrounds", StoreController::purchaseBackgrounds);
        RouteManager.addRoute("/myhabbo/store/purchase_stickie_notes", StoreController::purchaseStickieNotes);
        RouteManager.addRoute("/myhabbo/sticker/place_sticker", WidgetController::placeSticker);
        RouteManager.addRoute("/myhabbo/sticker/remove_sticker", WidgetController::removeSticker);
        RouteManager.addRoute("/myhabbo/widget/add", WidgetController::placeWidget);
        RouteManager.addRoute("/myhabbo/widget/delete", WidgetController::removeWidget);
        RouteManager.addRoute("/myhabbo/save", HomesController::save);

        // Homes
        RouteManager.addRoute("/home/*", HomesController::home);
        RouteManager.addRoute("/home/*/id", HomesController::home);
        RouteManager.addRoute("/myhabbo/widget/edit", WidgetController::editWidget);
        RouteManager.addRoute("/myhabbo/store/inventory", HomesController::inventory);
        RouteManager.addRoute("/myhabbo/store/inventory_items", HomesController::inventoryItems);
        RouteManager.addRoute("/myhabbo/store/inventory_preview", HomesController::inventoryPreview);
        RouteManager.addRoute("/myhabbo/tag/list", HomesController::tagList);
        RouteManager.addRoute("/myhabbo/noteeditor/editor", NoteEditorController::noteEditor);
        RouteManager.addRoute("/myhabbo/noteeditor/preview", NoteEditorController::notePreview);
        RouteManager.addRoute("/myhabbo/linktool/search", NoteEditorController::search);
        RouteManager.addRoute("/myhabbo/noteeditor/place", NoteEditorController::place);
        RouteManager.addRoute("/myhabbo/stickie/edit", NoteEditorController::stickieEdit);
        RouteManager.addRoute("/myhabbo/stickie/delete", NoteEditorController::stickieDelete);
        RouteManager.addRoute("/myhabbo/startSession/*", HomesController::startEditingSession);
        RouteManager.addRoute("/myhabbo/cancel/*", HomesController::cancelEditingSession);
        RouteManager.addRoute("/myhabbo/stickie/delete", NoteEditorController::stickieDelete);

        // Widgets
        RouteManager.addRoute("/myhabbo/rating/rate", RateController::rate);
        RouteManager.addRoute("/myhabbo/rating/reset_ratings", RateController::resetRating);
        RouteManager.addRoute("/myhabbo/badgelist/badgepaging", BadgesController::badgepaging);
        RouteManager.addRoute("/myhabbo/avatarlist/friendsearchpaging", FriendsWidgetController::friendsearchpaging);
        RouteManager.addRoute("/myhabbo/groups/groupinfo", GroupController::groupinfo);
        RouteManager.addRoute("/myhabbo/guestbook/preview", GuestbookController::preview);
        RouteManager.addRoute("/myhabbo/guestbook/add", GuestbookController::add);
        RouteManager.addRoute("/myhabbo/guestbook/remove", GuestbookController::remove);
        RouteManager.addRoute("/myhabbo/guestbook/configure", GuestbookController::configure);
        RouteManager.addRoute("/myhabbo/traxplayer/select_song", TraxController::selectSong);
        RouteManager.addRoute("/trax/song/*", TraxController::getSong);

        // Minimail
        RouteManager.addRoute("/minimail/loadMessages", MinimailController::loadMessages);
        RouteManager.addRoute("/minimail/recipients", MinimailController::recipients);
        RouteManager.addRoute("/minimail/preview", MinimailController::preview);
        RouteManager.addRoute("/minimail/sendMessage", MinimailController::sendMessage);
        RouteManager.addRoute("/minimail/loadMessage", MinimailController::loadMessage);
        RouteManager.addRoute("/minimail/deleteMessage", MinimailController::deleteMessage);
        RouteManager.addRoute("/minimail/undeleteMessage", MinimailController::undeleteMessage);
        RouteManager.addRoute("/minimail/emptyTrash", MinimailController::emptyTrash);

        // Quick menu
        RouteManager.addRoute("/quickmenu/groups", QuickmenuController::groups);
        RouteManager.addRoute("/quickmenu/rooms", QuickmenuController::rooms);
        RouteManager.addRoute("/quickmenu/friends_all", QuickmenuController::friends);

        // API
        RouteManager.addRoute("/api/advertisement/get_img", AdvertisementController::getImg);
        RouteManager.addRoute("/api/advertisement/get_url", AdvertisementController::getUrl);
        RouteManager.addRoute("/api/verify/get/*", VerifyController::get);
        RouteManager.addRoute("/api/verify/clear/*", VerifyController::clear);
        RouteManager.addRoute("/habbo-imaging/*", ImagerController::imager_redirect);

        // XML Promo habbos
        RouteManager.addRoute("/xml/promo_habbos.xml", XmlController::promoHabbos);
        RouteManager.addRoute("/xml/promo_habbos_v2.xml", XmlController::promoHabbosV2);

        // Housekeeping
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "", HousekeepingController::dashboard);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/", HousekeepingController::dashboard);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/login", HousekeepingController::login);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/api/ban", HousekeepingCommandsController::ban);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/logout", HousekeepingController::logout);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/transaction/lookup", HousekeepingTransactionsController::search);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/transaction/track_item", HousekeepingTransactionsController::item_lookup);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/users/search", HousekeepingUsersController::search);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/users/create", HousekeepingUsersController::create);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/users/edit", HousekeepingUsersController::edit);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/users/imitate/*", HousekeepingUsersController::imitate);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/infobus_polls", HousekeepingInfobusController::polls);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/infobus_polls/create", HousekeepingInfobusController::create_polls);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/infobus_polls/delete", HousekeepingInfobusController::delete);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/infobus_polls/edit", HousekeepingInfobusController::edit);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/infobus_polls/view_results", HousekeepingInfobusController::view_results);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/infobus_polls/clear_results", HousekeepingInfobusController::clear_results);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/infobus_polls/send_poll", HousekeepingInfobusController::send_poll);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/infobus_polls/close_event", HousekeepingInfobusController::close_event);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/infobus_polls/door_status", HousekeepingInfobusController::door_status);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/articles", HousekeepingNewsController::articles);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/articles/create", HousekeepingNewsController::create);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/articles/delete", HousekeepingNewsController::delete);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/articles/edit", HousekeepingNewsController::edit);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/configurations", HousekeepingConfigController::configurations);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/bans", HousekeepingBansController::bans);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/room_ads", HousekeepingAdsController::roomads);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/room_ads/delete", HousekeepingAdsController::delete);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/room_ads/create", HousekeepingAdsController::create);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/room_badges", HousekeepingRoomBadgesController::badges);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/room_badges/delete", HousekeepingRoomBadgesController::delete);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/room_badges/create", HousekeepingRoomBadgesController::create);
        RouteManager.addRoute("/" + HOUSEKEEPING_PATH + "/catalogue/edit_frontpage", HousekeepingCatalogueFrontpageController::edit);
    }
}
