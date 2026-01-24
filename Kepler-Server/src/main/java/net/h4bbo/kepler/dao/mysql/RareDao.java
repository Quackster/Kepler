package net.h4bbo.kepler.dao.mysql;

import net.h4bbo.kepler.dao.Storage;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RareDao {
    public static void addRare(String sprite, long reuseTime) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO rare_cycle (sale_code, reuse_time) VALUES (?, ?)", sqlConnection);

            preparedStatement.setString(1, sprite);
            preparedStatement.setLong(2, reuseTime);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void removeRares(List<String> sprites) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM rare_cycle WHERE sale_code = ?", sqlConnection);
            sqlConnection.setAutoCommit(false);

            for (String sprite : sprites) {
                preparedStatement.setString(1, sprite);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            sqlConnection.setAutoCommit(true);
        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static Map<String, Long> getUsedRares() throws SQLException {
        Map<String, Long> rares = new LinkedHashMap<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String itemSaleCode = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT sale_code, reuse_time FROM rare_cycle ORDER BY reuse_time DESC", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                rares.put(resultSet.getString("sale_code"), resultSet.getLong("reuse_time"));
            }
        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return rares;
    }

    public static Pair<String, Long> getCurrentRare() throws SQLException {
        Pair<String, Long> itemData = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT sale_code, reuse_time FROM rare_cycle ORDER BY reuse_time DESC LIMIT 1", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                itemData = Pair.of(resultSet.getString("sale_code"), resultSet.getLong("reuse_time"));
            }
        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return itemData;
    }
}
