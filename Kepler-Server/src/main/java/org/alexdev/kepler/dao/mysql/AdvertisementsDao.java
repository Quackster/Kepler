package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.ads.Advertisement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.*;

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

    public static void updateAds(Collection<Advertisement> ads) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE rooms_ads SET room_id = ?, image = ?, url = ?, enabled = ?, is_loading_ad = ? WHERE id = ?", sqlConnection);
            sqlConnection.setAutoCommit(false);

            for (var ad : ads) {
                preparedStatement.setInt(1, ad.getRoomId());

                if (ad.getImage().isBlank()) {
                    preparedStatement.setNull(2, Types.VARCHAR);
                } else {
                    preparedStatement.setString(2, ad.getImage());
                }

                if (ad.getUrl().isBlank()) {
                    preparedStatement.setNull(3, Types.VARCHAR);
                } else {
                    preparedStatement.setString(3, ad.getUrl());
                }

                preparedStatement.setBoolean(4, ad.isEnabled());
                preparedStatement.setBoolean(5, ad.isLoadingAd());
                preparedStatement.setInt(6, ad.getId());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            sqlConnection.setAutoCommit(true);

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void deleteAd(int id) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM rooms_ads WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void create(int roomId, String url, String image, boolean isEnabled, boolean isRoomLoadingAd) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO rooms_ads (room_id, url, image, enabled, is_loading_ad) VALUES (?, ?, ?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, roomId);

            if (url.isBlank()) {
                preparedStatement.setNull(2, Types.VARCHAR);
            } else {
                preparedStatement.setString(2, url);
            }

            if (image.isBlank()) {
                preparedStatement.setNull(3, Types.VARCHAR);
            } else {
                preparedStatement.setString(3, image);
            }

            preparedStatement.setInt(4, isEnabled ? 1 : 0);
            preparedStatement.setInt(5, isRoomLoadingAd ? 1 : 0);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}
