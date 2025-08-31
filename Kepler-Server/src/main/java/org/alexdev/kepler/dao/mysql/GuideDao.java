package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.player.guides.GuidingData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GuideDao {
    public static List<GuidingData> getGuidedBy(int userId) {
        List<GuidingData> users = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT id, username, last_online, online_time FROM users_statistics INNER JOIN users ON users.id = users_statistics.user_id WHERE guided_by = " + userId, sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                users.add(new GuidingData(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getTime("last_online").getTime() / 1000L,
                        resultSet.getLong("online_time")));
            }
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return users;
    }
}
