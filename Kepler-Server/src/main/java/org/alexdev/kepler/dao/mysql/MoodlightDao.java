package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.item.Item;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MoodlightDao {
    
    /**
     * Get if there's a preset row for the moodlight.
     */
    public static boolean containsPreset(int itemId) {
        boolean hasPreset = false;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT item_id FROM items_moodlight_presets WHERE item_id = ?", sqlConnection);
            preparedStatement.setInt(1, itemId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                hasPreset = true;
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return hasPreset;
    }

    public static boolean createPresets(int itemId) {
        boolean hasPreset = false;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO items_moodlight_presets (item_id) VALUES (?)", sqlConnection);
            preparedStatement.setInt(1, itemId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return hasPreset;
    }

    public static boolean updatePresets(int itemId, int currentPreset, List<String> presetData) {
        boolean hasPreset = false;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE items_moodlight_presets SET current_preset = ?, preset_1 = ?, preset_2 = ?, preset_3 = ? WHERE item_id = ?", sqlConnection);
            preparedStatement.setInt(1, currentPreset);
            preparedStatement.setString(2, presetData.get(0));
            preparedStatement.setString(3, presetData.get(1));
            preparedStatement.setString(4, presetData.get(2));
            preparedStatement.setInt(5, itemId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return hasPreset;
    }

    public static boolean deletePresets(int itemId) {
        boolean hasPreset = false;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM items_moodlight_presets WHERE item_id = ?", sqlConnection);
            preparedStatement.setInt(1, itemId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return hasPreset;
    }

    public static Pair<Integer, ArrayList<String>> getPresets(int itemId) {
        Pair<Integer, ArrayList<String>> presetData = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM items_moodlight_presets WHERE item_id = ? LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, itemId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                ArrayList<String> presets = new ArrayList<>();
                presets.add(resultSet.getString("preset_1"));
                presets.add(resultSet.getString("preset_2"));
                presets.add(resultSet.getString("preset_3"));

                presetData = Pair.of(resultSet.getInt("current_preset"), presets);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return presetData;
    }
}
