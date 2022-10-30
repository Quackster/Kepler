package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.ads.Advertisement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvertisementsDao {
    public static Map<Integer, List<Advertisement>> getAds()  {
        Map<Integer, List<Advertisement>> roomAds = new HashMap<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM rooms_ads", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int roomId = resultSet.getInt("room_id");
                String image = resultSet.getString("image");
                String url = resultSet.getString("url");

                if (!roomAds.containsKey(roomId)) {
                    roomAds.put(roomId, new ArrayList<>());
                }

                roomAds.get(roomId).add(new Advertisement(resultSet.getInt("id"), resultSet.getBoolean("is_loading_ad"), roomId, image, url, resultSet.getBoolean("enabled")));
            }
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return roomAds;
    }
}
