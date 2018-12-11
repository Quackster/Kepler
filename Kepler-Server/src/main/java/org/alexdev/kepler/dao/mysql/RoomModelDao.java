package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.room.models.RoomModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ConcurrentHashMap;

public class RoomModelDao {
    public static ConcurrentHashMap<String, RoomModel> getModels() {
        ConcurrentHashMap<String, RoomModel> roomModels = new ConcurrentHashMap<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM rooms_models", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                RoomModel roomModel = new RoomModel(resultSet.getString("model_id"), resultSet.getString("model_name"),
                        resultSet.getInt("door_x"), resultSet.getInt("door_y"), resultSet.getDouble("door_z"),
                        resultSet.getInt("door_dir"), resultSet.getString("heightmap"), resultSet.getString("trigger_class"));

                roomModels.put(roomModel.getId(), roomModel);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return roomModels;
    }

}
