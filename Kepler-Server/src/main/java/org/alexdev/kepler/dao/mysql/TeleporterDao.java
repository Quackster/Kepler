package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeleporterDao {
    public static String getTeleporterId(String itemId) {
        String teleporterId = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT linked_id FROM items_teleporter_links WHERE item_id = ?", sqlConnection);
            preparedStatement.setString(1, itemId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                teleporterId = resultSet.getString("linked_id");
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return teleporterId;
    }

    public static void addPair(String itemId, String linkedId) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO items_teleporter_links (item_id, linked_id) VALUES (?, ?)", sqlConnection);
            preparedStatement.setString(1, itemId);
            preparedStatement.setString(2, linkedId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}
