package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.ban.Ban;
import org.alexdev.kepler.game.ban.BanType;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BanDao {
    public static Pair<String, Long> hasBan(BanType banType, String value) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Pair<String, Long> banned = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users_bans WHERE banned_value = ? AND ban_type = ? AND banned_until > CURRENT_TIMESTAMP() AND is_active = 1 ORDER BY banned_until DESC LIMIT 1", sqlConnection);
            preparedStatement.setString(1, value);
            preparedStatement.setString(2, banType.name());
            resultSet =  preparedStatement.executeQuery();

            if (resultSet.next()) {
                banned = Pair.of(
                        resultSet.getString("message"),
                        resultSet.getTime("banned_until").getTime() / 1000L
                );
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

    public static String getName(String machineId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String name = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT username FROM users WHERE machine_id = ?", sqlConnection);
            preparedStatement.setString(1, machineId);
            resultSet =  preparedStatement.executeQuery();

            if (resultSet.next()) {
                name = resultSet.getString("username");
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return name;
    }

    public static void addBan(BanType banType, String value, long bannedUntil, String message, int bannedBy) {
        if (value.isBlank())
            return;
        
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO users_bans (banned_value, ban_type, banned_until, message, banned_by) VALUES (?, ?, ?, ?, ?)", sqlConnection);
            preparedStatement.setString(1, value);
            preparedStatement.setString(2, banType.name());
            preparedStatement.setTimestamp(3, new java.sql.Timestamp(bannedUntil * 1000L));
            preparedStatement.setString(4, message);
            preparedStatement.setInt(5, bannedBy);
            preparedStatement.executeQuery();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(preparedStatement);
        }
    }

    public static void removeBan(BanType banType, String value) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            //preparedStatement = Storage.getStorage().prepare("DELETE FROM users_bans WHERE banned_value = ? AND ban_type = ?", sqlConnection);
            preparedStatement = Storage.getStorage().prepare("UPDATE users_bans SET is_active = 0 WHERE banned_value = ? AND ban_type = ?", sqlConnection);
            preparedStatement.setString(1, value);
            preparedStatement.setString(2, banType.name());
            preparedStatement.executeQuery();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(preparedStatement);
        }
    }

    public static List<Ban> getActiveBans(int page, String sortBy) {
        List<Ban> banList = new ArrayList<>();

        int rows = 25;
        int nextOffset = page * rows;

        if (nextOffset >= 0) {
            Connection sqlConnection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                sqlConnection = Storage.getStorage().getConnection();
                preparedStatement = Storage.getStorage().prepare("SELECT * FROM users_bans WHERE is_active = 1 ORDER BY " + sortBy + " DESC LIMIT ? OFFSET ?", sqlConnection);
                preparedStatement.setInt(1, rows);
                preparedStatement.setInt(2, nextOffset);

                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    banList.add(new Ban(BanType.valueOf(resultSet.getString("ban_type")), resultSet.getString("banned_value"), resultSet.getString("message"), resultSet.getTime("banned_until").getTime() / 1000L,
                            resultSet.getTime("banned_at").getTime() / 1000L, resultSet.getInt("banned_by")));
                }
            } catch (Exception e) {
                Storage.logError(e);
            } finally {
                Storage.closeSilently(sqlConnection);
                Storage.closeSilently(preparedStatement);
                Storage.closeSilently(resultSet);
            }
        }

        return banList;
    }
}
