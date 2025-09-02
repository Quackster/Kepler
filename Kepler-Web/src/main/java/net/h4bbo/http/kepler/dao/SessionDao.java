package net.h4bbo.http.kepler.dao;

import net.h4bbo.kepler.dao.Storage;
import net.h4bbo.http.kepler.game.account.ClientPreference;

import java.sql.*;

public class SessionDao {
    public static int getRememberToken(String token) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        int userId = 0;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT id FROM users WHERE remember_token = ?", sqlConnection);
            preparedStatement.setString(1, token);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                userId = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return userId;
    }

    public static void setRememberToken(int userId, String token) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET remember_token = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, token);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();
        } catch (SQLException e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void clearRememberToken(int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET remember_token = ? WHERE id = ?", sqlConnection);
            preparedStatement.setNull(1, Types.VARCHAR);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();
        } catch (SQLException e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void savePreferences(String motto, boolean profileVisibility, boolean showOnlineStatus, boolean wordFilterEnabled, boolean allowFriendRequests, boolean allowStalking, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET motto = ?, profile_visible = ?, online_status_visible = ?, wordfilter_enabled = ?, allow_friend_requests = ?, allow_stalking = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, motto);
            preparedStatement.setInt(2, profileVisibility ? 1 : 0);
            preparedStatement.setInt(3, showOnlineStatus ? 1 : 0);
            preparedStatement.setInt(4, wordFilterEnabled ? 1 : 0);
            preparedStatement.setInt(5, allowFriendRequests ? 1 : 0);
            preparedStatement.setInt(6, allowStalking ? 1 : 0);
            preparedStatement.setInt(7, userId);
            preparedStatement.execute();
        } catch (SQLException e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void saveTrade(int userId, boolean tradeSetting) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET trade_enabled = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, tradeSetting ? 1 : 0);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();
        } catch (SQLException e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}
