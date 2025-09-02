package net.h4bbo.http.kepler.dao;

import net.h4bbo.kepler.dao.Storage;
import net.h4bbo.kepler.game.messenger.MessengerUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FriendManagementDao {
    /**
     * Gets the friends through pagination.
     *
     * @param userId the user id
     * @return the friends
     */
    public static List<MessengerUser> getFriends(int userId, int page, int itemsPerPage) {
        List<MessengerUser> friends = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT id,username,figure,motto,last_online,sex,allow_stalking,is_online,category_id,online_status_visible FROM messenger_friends INNER JOIN users ON messenger_friends.from_id = users.id WHERE to_id = ? " +
                    "ORDER BY UNIX_TIMESTAMP(last_online) DESC LIMIT " + ((page - 1) * itemsPerPage) + "," + itemsPerPage, sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                friends.add(new MessengerUser(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("figure"),
                        resultSet.getString("sex"), resultSet.getString("motto"), resultSet.getTime("last_online").getTime() / 1000L,
                        resultSet.getBoolean("allow_stalking"), resultSet.getInt("category_id"),
                        resultSet.getBoolean("is_online"), resultSet.getBoolean("online_status_visible")));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return friends;
    }

    /**
     * Gets the friends through pagination.
     *
     * @param userId the user id
     * @return the friends
     */
    public static List<MessengerUser> getFriendsSearch(int userId, String searchQuery, int page, int itemsPerPage) {
        List<MessengerUser> friends = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT id,username,figure,motto,last_online,sex,allow_stalking,is_online,category_id,online_status_visible FROM messenger_friends INNER JOIN users ON messenger_friends.from_id = users.id WHERE to_id = ? " +
                    "AND username LIKE ? ORDER BY UNIX_TIMESTAMP(last_online) DESC LIMIT " + ((page - 1) * itemsPerPage) + "," + itemsPerPage, sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, searchQuery + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                friends.add(new MessengerUser(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("figure"),
                        resultSet.getString("sex"), resultSet.getString("motto"), resultSet.getTime("last_online").getTime() / 1000L,
                        resultSet.getBoolean("allow_stalking"), resultSet.getInt("category_id"),
                        resultSet.getBoolean("is_online"), resultSet.getBoolean("online_status_visible")));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return friends;
}

    /**
     * Gets the friends through pagination.
     *
     * @param userId the user id
     * @return the friends
     */
    public static int getFriendsCount(int userId, String searchQuery) {
        int friends = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT COUNT(*) as friends_amount,username FROM messenger_friends INNER JOIN users ON messenger_friends.from_id = users.id WHERE to_id = ? AND username LIKE ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, searchQuery + "%");
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                friends = resultSet.getInt("friends_amount");
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return friends;
    }

    /**
     * Counts the friends through search querying.
     *
     * @param userId the user id
     * @return the friends
     */
    public static int getFriendsCount(int userId) {
        int friends = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT count(*) as friends_amount FROM messenger_friends WHERE to_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                friends = resultSet.getInt("friends_amount");
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return friends;
    }
}
