package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.item.Photo;
import org.alexdev.kepler.util.DateUtil;

import java.sql.*;

public class PhotoDao {
    public static void addPhoto(int photoId, int userId, long timestamp, byte[] photo, int checksum) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO items_photos (photo_id, photo_user_id, timestamp, photo_data, photo_checksum) VALUES (?, ?, ?, ?, ?)", sqlConnection);

            Blob photoBlob = sqlConnection.createBlob();
            photoBlob.setBytes(1, photo);

            preparedStatement.setInt(1, photoId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setLong(3, timestamp);
            preparedStatement.setBlob(4, photoBlob);
            preparedStatement.setInt(5, checksum);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static Photo getPhoto(int photoId) throws SQLException {
        Photo photo = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM items_photos WHERE photo_id = ?", sqlConnection);// (photo_id, photo_user_id, timestamp, photo_data, photo_checksum) VALUES (?, ?, ?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, photoId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Blob photoBlob = resultSet.getBlob("photo_data");
                int blobLength = (int) photoBlob.length();

                byte[] photoBlobBytes = photoBlob.getBytes(1, blobLength);
                photo = new Photo(photoId, resultSet.getInt("photo_checksum"), photoBlobBytes, resultSet.getLong("timestamp"));
            }

        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return photo;
    }
}
