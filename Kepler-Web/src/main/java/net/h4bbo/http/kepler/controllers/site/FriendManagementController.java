package net.h4bbo.http.kepler.controllers.site;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.MessengerDao;
import net.h4bbo.kepler.game.messenger.MessengerCategory;
import net.h4bbo.kepler.game.messenger.MessengerUser;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.server.rcon.messages.RconHeader;
import net.h4bbo.http.kepler.dao.FriendManagementDao;
import net.h4bbo.http.kepler.util.RconUtil;
import net.h4bbo.http.kepler.util.XSSUtil;

import java.util.*;
import java.util.stream.Collectors;

public class FriendManagementController {
    public static void friendmanagement(Template template, WebConnection webConnection, int limit, int currentPage, int categoryId, String searchString) {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        var friendsCount = 0;
        List<MessengerUser> friends = new ArrayList<>();

        if (searchString != null) {
            friends = FriendManagementDao.getFriendsSearch(playerDetails.getId(), searchString, currentPage, limit).stream()
                    .sorted(Comparator.comparingLong(MessengerUser::getLastOnline).reversed())
                    .collect(Collectors.toList());
            friendsCount = FriendManagementDao.getFriendsCount(playerDetails.getId(), searchString);
        } else {
            friends = FriendManagementDao.getFriends(playerDetails.getId(), currentPage, limit).stream()
                    .sorted(Comparator.comparingLong(MessengerUser::getLastOnline).reversed())
                    .collect(Collectors.toList());
            friendsCount = FriendManagementDao.getFriendsCount(playerDetails.getId());
        }


        int pages = friendsCount > 0 ? (int) Math.ceil((double)friendsCount / (double)limit) : 0;

        if (pages == 0) {
            pages = 1;
        }

        var categories = MessengerDao.getCategories(playerDetails.getId());
        categories.sort(Comparator.comparingInt(MessengerCategory::getId));

        friends.forEach(friend -> {
            if (categories.stream().noneMatch(category -> friend.getCategoryId() == category.getId())) {
                friend.setCategoryId(0);
                MessengerDao.updateFriendCategory(playerDetails.getId(), friend.getUserId(), 0);
            }
        });

        if (categoryId > -1) {
            friends = friends.stream().filter(friend -> friend.getCategoryId() == categoryId).collect(Collectors.toList());
        }

        template.set("friends", friends);
        template.set("categories", categories);
        template.set("currentPage", currentPage);
        template.set("pageLimit", limit);
        
        if (currentPage >= 2) {
            template.set("firstPage", 1);
        } else {
            template.set("firstPage", -1);
        }

        if (currentPage > 1) {
            template.set("previousPage", currentPage - 1);
        } else {
            template.set("previousPage", -1);
        }

        if (pages >= (currentPage + 1)) {
            template.set("nextPage", currentPage + 1);
        } else {
            template.set("nextPage", -1);
        }

        if (pages >= (currentPage + 2)) {
            template.set("lastPage", pages);
        } else {
            template.set("lastPage", -1);
        }
    }

    public static void editCategory(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        var template = webConnection.template("profile/profile_widgets/friend_category_widget");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        String newName = webConnection.post().getString("name");
        int categoryId = webConnection.post().getInt("categoryId");

        if (!newName.isBlank()) {
            MessengerDao.updateCategory(newName, categoryId, playerDetails.getId());
        }

        RconUtil.sendCommand(RconHeader.REFRESH_MESSENGER_CATEGORIES, new HashMap<>() {{
            put("userId", playerDetails.getId());
        }});

        var categories = MessengerDao.getCategories(playerDetails.getId());
        categories.sort(Comparator.comparingInt(MessengerCategory::getId));

        template.set("categories", categories);
        template.render();
    }

    public static void createcategory(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        var template = webConnection.template("profile/profile_widgets/friend_category_widget");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        String newName = webConnection.post().getString("name");

        if (!newName.isBlank()) {
            if (newName.length() > 50) {
                newName = newName.substring(0, 50);
            }

            MessengerDao.addCategory(newName, playerDetails.getId());
        }

        RconUtil.sendCommand(RconHeader.REFRESH_MESSENGER_CATEGORIES, new HashMap<>() {{
            put("userId", playerDetails.getId());
        }});

        var categories = MessengerDao.getCategories(playerDetails.getId());
        categories.sort(Comparator.comparingInt(MessengerCategory::getId));

        template.set("categories", categories);
        template.render();
    }

    public static void deletecategory(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        var template = webConnection.template("profile/profile_widgets/friend_category_widget");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        int categoryId = webConnection.post().getInt("categoryId");
        MessengerDao.deleteCategory(categoryId, playerDetails.getId());

        RconUtil.sendCommand(RconHeader.REFRESH_MESSENGER_CATEGORIES, new HashMap<>() {{
            put("userId", playerDetails.getId());
        }});

        var categories = MessengerDao.getCategories(playerDetails.getId());
        categories.sort(Comparator.comparingInt(MessengerCategory::getId));

        MessengerDao.resetFriendCategories(playerDetails.getId(), categoryId);

        template.set("categories", categories);
        template.render();
    }

    public static void viewCategory(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int pageNumber = webConnection.get().contains("pageNumber") ? webConnection.get().getInt("pageNumber") : 1;
        int pageSize = webConnection.get().getInt("pageSize");
        int categoryId = webConnection.get().contains("categoryId") ? webConnection.get().getInt("categoryId") : -1;
        String searchString = webConnection.get().contains("searchString") ? webConnection.get().getString("searchString") : null;

        if (webConnection.get().queries().size() == 0) {
            pageSize = webConnection.post().getInt("pageSize");
            searchString = webConnection.post().contains("searchString") ? webConnection.post().getString("searchString") : null;
        }

        if (pageSize > 100 || pageSize <= 0) {
            pageSize = 30;
        }

        if (pageNumber <= 0) {
            pageNumber = 1;
        }

        Template template =  webConnection.template("profile/profile_widgets/friend_view_category");
        friendmanagement(template, webConnection, pageSize, pageNumber, categoryId, searchString);
        template.render();
    }

    public static void updateCategoryOptions(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        Template template =  webConnection.template("profile/profile_widgets/friend_category_options");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        template.set("categories", MessengerDao.getCategories(playerDetails.getId()));
        template.render();
    }

    public static void movefriends(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int pageSize = webConnection.post().getInt("pageSize");
        int categoryId = webConnection.post().contains("moveCategoryId") ? webConnection.post().getInt("moveCategoryId") : -1;

        if (pageSize > 100 || pageSize <= 0) {
            pageSize = 30;
        }

        Template template =  webConnection.template("profile/profile_widgets/friend_view_category");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        if (webConnection.post().contains("friendList[]")) {
            for (String value : webConnection.post().getArray("friendList[]")) {
                try {
                    int userId = Integer.parseInt(value);
                    MessengerDao.updateFriendCategory(playerDetails.getId(), userId, categoryId);
                } catch (Exception ex) {

                }
            }
        }

        RconUtil.sendCommand(RconHeader.REFRESH_MESSENGER_CATEGORIES, new HashMap<>() {{
            put("userId", playerDetails.getId());
        }});

        friendmanagement(template, webConnection, pageSize, 1, categoryId, null);
        template.render();
    }

    public static void deletefriends(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        Template template =  webConnection.template("profile/profile_widgets/friend_view_category");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        if (webConnection.post().contains("friendList[]")) {
            for (String value : webConnection.post().getArray("friendList[]")) {
                int userId = Integer.parseInt(value);

                MessengerDao.removeFriend(playerDetails.getId(), userId);
                MessengerDao.removeFriend(userId, playerDetails.getId());
            }

        }

        if (webConnection.post().contains("friendId")) {
            int userId = webConnection.post().getInt("friendId");

            MessengerDao.removeFriend(playerDetails.getId(), userId);
            MessengerDao.removeFriend(userId, playerDetails.getId());
        }

        RconUtil.sendCommand(RconHeader.REFRESH_MESSENGER_CATEGORIES, new HashMap<>() {{
            put("userId", playerDetails.getId());
        }});


        friendmanagement(template, webConnection, 30, 1, -1, null);
        template.render();
    }
}
