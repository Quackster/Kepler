package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.alerts.AccountAlert;
import org.alexdev.kepler.game.alerts.AlertType;
import org.alexdev.kepler.game.messenger.MessengerUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertsDao {
    public static List<AccountAlert> getAlerts(int userId) {
        List<AccountAlert> alerts = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_alerts WHERE user_id = ? ORDER BY created_at DESC", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                alerts.add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return alerts;
    }

    public static void createAlert(int userId, AlertType alertType, String message) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO cms_alerts (user_id, alert_type, message) VALUES (?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, alertType.name());
            preparedStatement.setString(3, message);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void deleteAlerts(int userId, AlertType alertType) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM cms_alerts WHERE user_id = ? AND alert_type = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, alertType.name());
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void disableAlerts(int userId, AlertType alertType) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE cms_alerts SET is_disabled = 1 WHERE user_id = ? AND alert_type = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, alertType.name());
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    private static AccountAlert fill(ResultSet resultSet) throws SQLException {
        return new AccountAlert(resultSet.getInt("id"), resultSet.getInt("user_id"), AlertType.valueOf(resultSet.getString("alert_type")),
                resultSet.getString("message"), resultSet.getBoolean("is_disabled"), resultSet.getTime("created_at").getTime() / 1000L);
    }

    public static Map<Integer, MessengerUser> getOnlineFriends(int userId) {
        Map<Integer, MessengerUser> friends = new HashMap<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT id,username,figure,motto,last_online,sex,allow_stalking,is_online,category_id,online_status_visible FROM messenger_friends INNER JOIN users ON messenger_friends.from_id = users.id WHERE to_id = ? AND is_online = 1", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int resultUserId = resultSet.getInt("id");
                friends.put(resultUserId, new MessengerUser(resultUserId, resultSet.getString("username"), resultSet.getString("figure"),
                        resultSet.getString("sex"), resultSet.getString("console_motto"), resultSet.getTime("last_online").getTime() / 1000L,
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

    public static int countRequests(int userId) {
        int count = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT COUNT(*) FROM messenger_requests WHERE to_id = " + userId, sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
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

    public static void deleteAlert(int userId, int id) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM cms_alerts WHERE user_id = ? AND id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}
