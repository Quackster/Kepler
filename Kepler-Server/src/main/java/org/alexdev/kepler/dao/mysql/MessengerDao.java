package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.messenger.MessengerCategory;
import org.alexdev.kepler.game.messenger.MessengerMessage;
import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessengerDao {

    /**
     * Gets the friends.
     *
     * @param userId the user id
     * @return the friends
     */
    public static Map<Integer, MessengerUser> getFriends(int userId) {
        Map<Integer, MessengerUser> friends = new HashMap<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT id,username,figure,motto,last_online,sex,allow_stalking,is_online,category_id,online_status_visible FROM messenger_friends INNER JOIN users ON messenger_friends.from_id = users.id WHERE to_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int resultUserId = resultSet.getInt("id");
                friends.put(resultUserId, new MessengerUser(resultUserId, resultSet.getString("username"), resultSet.getString("figure"),
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

    public static Map<Integer, MessengerUser> getFriendsPage(int userId, int range, int pageSize) {
        Map<Integer, MessengerUser> friends = new HashMap<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT id,username,figure,motto,last_online,sex,allow_stalking,is_online,category_id,online_status_visible FROM messenger_friends INNER JOIN users ON messenger_friends.from_id = users.id WHERE to_id = ? LIMIT " + (range * pageSize) + "," + ((range * pageSize) + pageSize), sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int resultUserId = resultSet.getInt("id");
                friends.put(resultUserId, new MessengerUser(resultUserId, resultSet.getString("username"), resultSet.getString("figure"),
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

    public static int getFriendsCount(int userId) {
        int count = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT COUNT(*) FROM messenger_friends INNER JOIN users ON messenger_friends.from_id = users.id WHERE to_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return count;
    }

    /**
     * Gets the requests.
     *
     * @param userId the user id
     * @return the requests
     */
    public static Map<Integer, MessengerUser> getRequests(int userId) {
        Map<Integer, MessengerUser> users = new HashMap<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT from_id,username,figure,sex,motto,last_online,allow_stalking,is_online,online_status_visible FROM messenger_requests INNER JOIN users ON messenger_requests.from_id = users.id WHERE to_id = " + userId, sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int fromId = resultSet.getInt("from_id");
                users.put(fromId, new MessengerUser(fromId, resultSet.getString("username"), resultSet.getString("figure"),
                        resultSet.getString("sex"), resultSet.getString("motto"), resultSet.getTime("last_online").getTime() / 1000L,
                        resultSet.getBoolean("allow_stalking"), 0,
                        resultSet.getBoolean("is_online"), resultSet.getBoolean("online_status_visible")));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return users;
    }

    /**
     * Search.
     *
     * @param query the query
     * @return the list
     */
    public static List<Integer> search(String query) {
        List<Integer> userList = new ArrayList<>();
        int userId = -1;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();

            preparedStatement = Storage.getStorage().prepare("SELECT id FROM users WHERE LOWER(username) LIKE ? LIMIT 30", sqlConnection);
            preparedStatement.setString(1, query + "%");

            /* preparedStatement = Storage.getStorage().prepare("SELECT id FROM users WHERE LOWER(username) LIKE ? ORDER BY (username = ?) DESC, length(username) LIMIT 30", sqlConnection);
            preparedStatement.setString(1, query + "%");
            preparedStatement.setString(2, query);*/

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                userList.add(resultSet.getInt("id"));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return userList;
    }

    /**
     * New request.
     *
     * @param fromId the from id
     * @param toId the to id
     */
    public static void newRequest(int fromId, int toId) {
        if (toId == fromId) {
            return;
        }

        if (requestExists(fromId, toId)) {
            return;
        }

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO messenger_requests (to_id, from_id) VALUES (?, ?)", sqlConnection);
            preparedStatement.setInt(1, toId);
            preparedStatement.setInt(2, fromId);
            preparedStatement.execute();
        } catch (SQLException ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Get if the request exists.
     *
     * @param fromId the from id
     * @param toId the to id
     *
     * @return true, if successful
     */
    public static boolean requestExists(int fromId, int toId) {
        boolean exists = false;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM messenger_requests WHERE from_id = ? AND to_id = ?", sqlConnection);
            preparedStatement.setInt(1, fromId);
            preparedStatement.setInt(2, toId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                exists = true;
            }

        } catch (Exception ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return exists;
    }

    /**
     * Get if friend exists.
     *
     * @param fromId the from id
     * @param toId the to id
     *
     * @return true, if successful
     */
    public static boolean friendExists(int fromId, int toId) {
        boolean exists = false;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM messenger_friends WHERE from_id = ? AND to_id = ?", sqlConnection);
            preparedStatement.setInt(1, fromId);
            preparedStatement.setInt(2, toId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                exists = true;
            }

        } catch (Exception ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return exists;
    }

    /**
     * Removes the request.
     *
     * @param fromId the from id
     * @param toId the to id
     */
    public static void removeRequest(int fromId, int toId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM messenger_requests WHERE from_id = ? AND to_id = ?", sqlConnection);
            preparedStatement.setInt(1, fromId);
            preparedStatement.setInt(2, toId);
            preparedStatement.execute();

            preparedStatement = Storage.getStorage().prepare("DELETE FROM messenger_requests WHERE from_id = ? AND to_id = ?", sqlConnection);
            preparedStatement.setInt(1, toId);
            preparedStatement.setInt(2, fromId);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void removeAllRequests(int toId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM messenger_requests WHERE to_id = ?", sqlConnection);
            preparedStatement.setInt(1, toId);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Removes the friend.
     *
     * @param toId the friend id
     * @param fromId the user id
     */
    public static void removeFriend(int toId, int fromId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM messenger_friends WHERE from_id = ? AND to_id = ?", sqlConnection);
            preparedStatement.setInt(1, fromId);
            preparedStatement.setInt(2, toId);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * New friend.
     *
     * @param fromId the sender
     * @param toId the receiver
     */
    public static void newFriend(int toId, int fromId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO messenger_friends (from_id, to_id) VALUES (?, ?)", sqlConnection);
            preparedStatement.setInt(1, fromId);
            preparedStatement.setInt(2, toId);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Removes the friend.
     *
     * @param userId the friend id
     * @param friendId the user id
     */
    public static void updateFriendCategory(int userId, int friendId, int categoryId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE messenger_friends SET category_id = ? WHERE from_id = ? AND to_id = ?", sqlConnection);
            preparedStatement.setInt(1, categoryId);
            preparedStatement.setInt(2, friendId);
            preparedStatement.setInt(3, userId);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Removes the category from friends after it had been deleted
     *
     * @param userId the friend id
     * @param categoryId the category
     */
    public static void resetFriendCategories(int userId, int categoryId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE messenger_friends SET category_id = 0 WHERE to_id = ? AND category_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, categoryId);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }


    /**
     * Create a message for other people to read them later, if they're offline.
     *
     * @param fromId the id the user sending the message
     * @param toId the id of the user to receive it
     * @param message the body of the message
     * @return the id of the message
     */
    public static int newMessage(int fromId, int toId, String message) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        int messageID = 0;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO messenger_messages (receiver_id, sender_id, unread, body, date) VALUES (?, ?, ?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, toId);
            preparedStatement.setInt(2, fromId);
            preparedStatement.setInt(3, 1);
            preparedStatement.setString(4, message);
            preparedStatement.setLong(5, DateUtil.getCurrentTimeSeconds());
            preparedStatement.executeUpdate();

            ResultSet row = preparedStatement.getGeneratedKeys();

            if (row != null && row.next()) {
                messageID = row.getInt(1);
            }

        } catch (SQLException e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return messageID;
    }

    /**
     * Get unread messages for the user.
     *
     * @param userId the id of the user to get the offline messages for
     * @return the list of messages
     */
    public static Map<Integer, MessengerMessage> getUnreadMessages(int userId) {
        Map<Integer, MessengerMessage> messages = new HashMap<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM messenger_messages WHERE receiver_id = " + userId + " AND unread = 1", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                messages.put(resultSet.getInt("id"), new MessengerMessage(
                        resultSet.getInt("id"), resultSet.getInt("receiver_id"), resultSet.getInt("sender_id"),
                        resultSet.getLong("date"), resultSet.getString("body")));

            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return messages;
    }

    /**
     * Mark a message as read.
     *
     * @param messageId the message id to reset
     */
    public static void markMessageRead(int messageId) throws SQLException {
        Storage.getStorage().execute("UPDATE messenger_messages SET unread = 0 WHERE id = " + messageId);
    }

    public static List<MessengerCategory> getCategories(int userId) {
        var categories = new ArrayList<MessengerCategory>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM messenger_categories WHERE user_id  = " + userId, sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                categories.add(new MessengerCategory(resultSet.getInt("id"), resultSet.getInt("user_id"), resultSet.getString("name")));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return categories;
    }

    public static void deleteCategory(int categoryId, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM messenger_categories WHERE id = ? AND user_id = ?", sqlConnection);
            preparedStatement.setInt(1, categoryId);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void addCategory(String name, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO messenger_categories (user_id, name) VALUES (?, ?)", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, name);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void updateCategory(String name, int categoryId, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE messenger_categories SET name = ? WHERE id = ? AND user_id = ?", sqlConnection);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, categoryId);
            preparedStatement.setInt(3, userId);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}