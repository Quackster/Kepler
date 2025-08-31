package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.messenger.MessengerCategory;
import org.alexdev.kepler.game.messenger.MessengerMessage;
import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.game.player.Wardrobe;
import org.alexdev.kepler.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WardrobeDao {
    public static List<Wardrobe> getWardrobe(int userId) {
        List<Wardrobe> wardrobeList = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users_wardrobes WHERE user_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                wardrobeList.add(new Wardrobe(resultSet.getInt("slot_id"), resultSet.getString("sex"), resultSet.getString("figure")));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return wardrobeList;
    }

    public static void deleteWardrobe(int userId, int slotId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM users_wardrobes WHERE user_id = ? AND slot_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, slotId);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void addWardrobe(int userId, int slotId, String figure, String sex) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO users_wardrobes (user_id, slot_id, figure, sex) VALUES (?, ?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, slotId);
            preparedStatement.setString(3, figure);
            preparedStatement.setString(4, sex);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void updateWardrobe(int userId, int slotId, String figure, String sex) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users_wardrobes SET figure = ?, sex = ? WHERE slot_id = ? AND user_id = ?", sqlConnection);
            preparedStatement.setString(1, figure);
            preparedStatement.setString(2, sex);
            preparedStatement.setInt(3, slotId);
            preparedStatement.setInt(4, userId);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}
