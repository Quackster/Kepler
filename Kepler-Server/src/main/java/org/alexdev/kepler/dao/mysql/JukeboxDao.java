package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JukeboxDao {
    public static void addDisk(long itemId, int slotId, int songId) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO soundmachine_disks (item_id, slot_id, song_id, burned_at) VALUES (?, ?, ?, ?)", sqlConnection);
            preparedStatement.setLong(1, itemId);
            preparedStatement.setInt(2, slotId);
            preparedStatement.setInt(3, songId);
            preparedStatement.setLong(4, DateUtil.getCurrentTimeSeconds());
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void editDisk(long itemId, int songMachineId, int slotId) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE soundmachine_disks SET soundmachine_id = ?, slot_id = ? WHERE item_id = ?", sqlConnection);
            preparedStatement.setInt(1, songMachineId);
            preparedStatement.setInt(2, slotId);
            preparedStatement.setLong(3, itemId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static List<Integer> getItemsBySong(int songId) {
        List<Integer> items = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM soundmachine_disks", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                items.add(resultSet.getInt("item_id"));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return items;
    }

    public static int getSongIdByItem(long itemId) {
        int songId = -1;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM soundmachine_disks WHERE item_id = ?", sqlConnection);
            preparedStatement.setLong(1, itemId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
               songId = resultSet.getInt("song_id");
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return songId;
    }

    public static void setBurned(int songId, boolean burnedState) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE soundmachine_songs SET burnt = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, burnedState ? 1 : 0);
            preparedStatement.setInt(2, songId);
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
