package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.ban.BanType;
import org.alexdev.kepler.util.DateUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BanDao {
    public static Pair<String, Long> hasBan(BanType banType, Integer number) {
        return hasBan(banType, String.valueOf(number));
    }

    public static Pair<String, Long> hasBan(BanType banType, String value) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Pair<String, Long> banned = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * from users_bans WHERE banned_value = ? AND ban_type = ? LIMIT 1", sqlConnection);
            preparedStatement.setString(1, value);
            preparedStatement.setString(2, banType.name());
            resultSet =  preparedStatement.executeQuery();

            if (resultSet.next()) {
                if (DateUtil.getCurrentTimeSeconds() < resultSet.getLong("banned_until")) {
                    banned = Pair.of(
                            resultSet.getString("message"),
                            resultSet.getLong("banned_until")
                    );
                }
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return banned;
    }

    public static void addBan(BanType banType, String value, long bannedUntil, String message, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO users_bans (banned_value, ban_type, banned_until, message, user_id) VALUES (?, ?, ?, ?)", sqlConnection);
            preparedStatement.setString(1, value);
            preparedStatement.setString(2, banType.name());
            preparedStatement.setLong(3, bannedUntil);
            preparedStatement.setString(4, message);
            preparedStatement.setInt(5, userId);
            preparedStatement.executeQuery();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(preparedStatement);
        }
    }
}
