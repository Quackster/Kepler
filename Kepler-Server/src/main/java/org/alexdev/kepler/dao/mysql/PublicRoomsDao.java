package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.item.public_items.PublicItemData;
import org.alexdev.kepler.game.room.handlers.walkways.WalkwaysEntrance;
import org.alexdev.kepler.game.room.handlers.walkways.WalkwaysManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PublicRoomsDao {
    /**
     * Get the item definitions.
     *
     * @return the list of item definitions
     */
    public static List<PublicItemData> getPublicItemData(String roomModel) {
       List<PublicItemData> itemDataList = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM public_items WHERE room_model = ?", sqlConnection);
            preparedStatement.setString(1, roomModel);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                PublicItemData itemData = new PublicItemData(resultSet.getString("id"), resultSet.getString("room_model"),
                        resultSet.getString("sprite"), resultSet.getInt("x"), resultSet.getInt("y"),
                        resultSet.getDouble("z"), resultSet.getInt("rotation"),  resultSet.getDouble("top_height"),
                        resultSet.getInt("length"), resultSet.getInt("width"), resultSet.getString("behaviour"),
                        resultSet.getString("current_program"), resultSet.getString("teleport_to"), resultSet.getString("swim_to"));

                itemDataList.add(itemData);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return itemDataList;
    }

    public static List<WalkwaysEntrance> getWalkways() {
        List<WalkwaysEntrance> walkwaysEntrances = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM public_roomwalkways", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                walkwaysEntrances.add(WalkwaysManager.createWalkway(resultSet.getInt("room_id"), resultSet.getInt("to_id"), resultSet.getString("coords_map"),
                        resultSet.getString("door_position")));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return walkwaysEntrances;
    }
}
