package net.h4bbo.kepler.dao.mysql;

import net.h4bbo.kepler.dao.Storage;
import net.h4bbo.kepler.game.ads.Advertisement;
import net.h4bbo.kepler.game.catalogue.collectables.CollectableData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.*;

public class CollectablesDao {
    public static List<CollectableData> getCollectablesData() {
        List<CollectableData> collectableData = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM catalogue_collectables", sqlConnection);
            resultSet =  preparedStatement.executeQuery();

            while (resultSet.next()) {
                collectableData.add(new CollectableData(resultSet.getInt("store_page"), resultSet.getInt("admin_page"), resultSet.getLong("expiry"),
                        resultSet.getLong("lifetime"), resultSet.getInt("current_position"), resultSet.getString("class_names").split(",")));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return collectableData;
    }

    public static void saveData(int storePage, int currentPosition, long expiry) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE catalogue_collectables SET expiry = ?, current_position = ? WHERE store_page = ?", sqlConnection);
            preparedStatement.setLong(1, expiry);
            preparedStatement.setInt(2, currentPosition);
            preparedStatement.setInt(3, storePage);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}
