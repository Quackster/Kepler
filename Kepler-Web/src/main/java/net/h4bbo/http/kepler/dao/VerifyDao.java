package net.h4bbo.http.kepler.dao;

import net.h4bbo.kepler.dao.Storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VerifyDao {
    public static String getName(String verifyCode) {
        String name = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT username FROM users_statistics INNER JOIN users ON users_statistics.user_id = users.id WHERE verify_code = ? LIMIT 1", sqlConnection);
            preparedStatement.setString(1, verifyCode);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                name = resultSet.getString("username");
            }
        } catch (SQLException e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return name;
    }

    public static void clearName(String verifyCode) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users_statistics SET verify_code = NULL WHERE verify_code = ? LIMIT 1", sqlConnection);
            preparedStatement.setString(1, verifyCode);
            preparedStatement.execute();
        } catch (SQLException e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}
