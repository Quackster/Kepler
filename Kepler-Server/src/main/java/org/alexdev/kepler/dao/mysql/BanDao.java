package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.ban.BanType;
import org.alexdev.kepler.game.ban.BannedPlayer;
import org.alexdev.kepler.util.DateUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BanDao {
    public static BannedPlayer hasBan(String ip, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        BannedPlayer banned = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users_bans WHERE (ip = ? OR user_id = ?) AND banned_until > ? ORDER BY FIELD(ban_type, 'IP_ADDRESS', 'USER_ID') LIMIT 1;", sqlConnection);
            preparedStatement.setString(1, ip);
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, DateUtil.getCurrentTimeSeconds());
            resultSet =  preparedStatement.executeQuery();

            if (resultSet.next()) {
                if (DateUtil.getCurrentTimeSeconds() < resultSet.getLong("banned_until")) {
                    banned = new BannedPlayer(
                            resultSet.getInt("id"),
                            resultSet.getString("message"),
                            resultSet.getString("ip"),
                            resultSet.getInt("user_id"),
                            BanType.valueOf(resultSet.getString("ban_type")),
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

    public static int addBan(BannedPlayer bannedPlayer) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        int banId = 0;
        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO users_bans (ip, ban_type, banned_until, message, user_id) VALUES (?, ?, ?, ?, ?)", sqlConnection);
            preparedStatement.setString(1, bannedPlayer.getIp());
            preparedStatement.setString(2, bannedPlayer.getBanType().name());
            preparedStatement.setLong(3, bannedPlayer.getBannedUntil());
            preparedStatement.setString(4, bannedPlayer.getReason());
            preparedStatement.setInt(5, bannedPlayer.getUserId());
            preparedStatement.executeQuery();

            ResultSet row = preparedStatement.getGeneratedKeys();

            if (row != null && row.next()) {
                banId = row.getInt(1);
            }


        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(preparedStatement);
        }
        return banId;
    }
}
