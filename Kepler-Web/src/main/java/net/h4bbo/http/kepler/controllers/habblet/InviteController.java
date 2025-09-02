package net.h4bbo.http.kepler.controllers.habblet;

import net.h4bbo.kepler.game.messenger.MessengerManager;
import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.MessengerDao;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.messenger.Messenger;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.server.rcon.messages.RconHeader;
import net.h4bbo.kepler.util.StringUtil;
import net.h4bbo.http.kepler.util.RconUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class InviteController {
    public static void inviteLink(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        //PlayerDetails playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));

        Template template = webConnection.template("habblet/invite_referralLink");
        template.render();
    }

    public static void searchContent(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        Template template = webConnection.template("habblet/invite_searchContent");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        Messenger messenger = new Messenger(playerDetails);

        String searchString = webConnection.post().getString("searchString");
        int pageId = webConnection.post().contains("pageNumber") ? webConnection.post().getInt("pageNumber") - 1 : 0;
        int nextPageId = -1;
        int previousPageId = -1;

        List<PlayerDetails> searchedFriends = new ArrayList<>();

        for (int userId : MessengerDao.search(searchString)) {
            if (playerDetails.getId() == userId) {
                continue;
            }

            searchedFriends.add(PlayerDao.getDetails(userId));
        }

        searchedFriends.sort(Comparator.comparing(PlayerDetails::getName));

        var searchMap = StringUtil.paginate(searchedFriends, 5);
        List<PlayerDetails> searchResults = null;

        if (searchMap.containsKey(pageId)) {
            searchResults = searchMap.get(pageId);
        } else {
            searchResults = new ArrayList<>();
        }

        if (searchMap.containsKey(pageId - 1)) {
            previousPageId = pageId -1;
        }

        if (searchMap.containsKey(pageId + 1)) {
            nextPageId = pageId + 1;
        }

        nextPageId = nextPageId > -1 ? nextPageId + 1 : -1;
        previousPageId = previousPageId > -1 ? previousPageId + 1 : -1;

        template.set("searchResults", searchResults);
        template.set("currentPage", pageId + 1);
        template.set("totalPages", searchMap.size());
        template.set("previousPageId", previousPageId);
        template.set("nextPageId",  nextPageId);
        template.set("messenger", messenger);
        template.render();
    }

    public static void confirmAddFriend(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        Template template = webConnection.template("habblet/invite_confirmAddFriend");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        if (playerDetails == null) {
            webConnection.send("");
            return;
        }

        template.set("username", playerDetails.getName());
        template.render();
    }

    public static void addFriend(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int accountId = 0;

        try {
            accountId = webConnection.post().getInt("accountId");
        } catch (Exception ex) {

        }

        Template template = webConnection.template("habblet/invite_addFriend");
        template.set("message", createFriendRequestResponse(webConnection, accountId));
        template.render();
    }

    public static void add(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int accountId = 0;

        try {
            accountId = webConnection.post().getInt("accountId");
        } catch (Exception ex) {

        }

        webConnection.send(ResponseBuilder.create("application/x-javascript", "Dialog.showInfoDialog(\"add-friend-messages\", \"" + createFriendRequestResponse(webConnection, accountId) + "\", \"OK\");"));
    }

    public static String createFriendRequestResponse(WebConnection webConnection, int accountId) {
        String response;

        Messenger target = MessengerManager.getInstance().getMessengerData(accountId);
        Messenger callee = MessengerManager.getInstance().getMessengerData(webConnection.session().getInt("user.id"));

        if (target == null) {
            //player.send(new MESSENGER_ERROR(new MessengerError(MessengerErrorType.FRIEND_REQUEST_NOT_FOUND)));
            response = "There was an error finding the user for the friend request.";
        } else {
            if (target.getMessengerUser().getUsername().equalsIgnoreCase("Abigail.Ryan")) {
                target = null;
            }

            if (target == null) {
                //player.send(new MESSENGER_ERROR(new MessengerError(MessengerErrorType.FRIEND_REQUEST_NOT_FOUND)));
                response = "There was an error finding the user for the friend request.";
            } else if (callee.isFriendsLimitReached()) {
                //player.send(new MESSENGER_ERROR(new MessengerError(MessengerErrorType.FRIENDLIST_FULL)));
                response = "Your friends list is full.";
            } else if (target.hasFriend(webConnection.session().getInt("user.id"))) {
                response = "This person is already your friend";
            } else if (target.hasRequest(webConnection.session().getInt("user.id"))) {
                response = "There is already a friend request for this user.";
            } else if (target.isFriendsLimitReached()) {
                response = "This user's friend list is full.";
            } else if (!target.isAllowsFriendRequests()) {
                //player.send(new MESSENGER_ERROR(new MessengerError(MessengerErrorType.TARGET_DOES_NOT_ACCEPT)));
                //return;
                response = "This user does not accept friend requests at the moment.";
            } else if (webConnection.post().getInt("accountId") == webConnection.session().getInt("user.id")) {
                response = "There was an error processing your request.";
            } else {
                response = "Friend request has been sent successfully.";
                target.addRequest(callee.getMessengerUser());

                RconUtil.sendCommand(RconHeader.FRIEND_REQUEST, new HashMap<>() {{
                    put("userId", webConnection.session().getInt("user.id"));
                    put("friendId", webConnection.post().getInt("accountId"));
                }});
            }
        }

        return response;
    }
}
