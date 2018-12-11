package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.navigator.NavigatorCategory;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoomRightsDao {

    /**
     * Get a list of users with room rights for a room
     *
     * @return the list of user ids who have room rights
     */
    public static List<Integer> getRoomRights(RoomData room) {
        List<Integer> users = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT user_id FROM rooms_rights WHERE room_id = ?", sqlConnection);
            preparedStatement.setInt(1, room.getId());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                users.add(resultSet.getInt("user_id"));
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

    public static void addRights(PlayerDetails user, RoomData room) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO rooms_rights (user_id, room_id) VALUES (?, ?)", sqlConnection);
            preparedStatement.setInt(1, user.getId());
            preparedStatement.setInt(2, room.getId());
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void removeRights(PlayerDetails user, RoomData room) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM rooms_rights WHERE user_id = ? AND room_id = ?", sqlConnection);
            preparedStatement.setInt(1, user.getId());
            preparedStatement.setInt(2, room.getId());
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void deleteRoomRights(RoomData room) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM rooms_rights WHERE room_id = ?", sqlConnection);
            preparedStatement.setInt(1, room.getId());
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}
