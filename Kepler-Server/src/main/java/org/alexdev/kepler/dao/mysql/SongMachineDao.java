package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.item.ItemManager;
import org.alexdev.kepler.game.item.base.ItemDefinition;
import org.alexdev.kepler.game.song.Song;
import org.alexdev.kepler.game.song.SongPlaylist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongMachineDao {

    /**
     * Get the song list for this machine.
     *
     * @param itemId the item id for this machine
     * @return the list of songs
     */
    public static List<Song> getSongList(int itemId) {
        List<Song> songs = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM soundmachine_songs WHERE item_id = ?", sqlConnection);
            preparedStatement.setInt(1, itemId);
            resultSet = preparedStatement.executeQuery();

            // (int id, String title, int itemId, int length, String data, boolean isBurnt)
            while (resultSet.next()) {
                songs.add(new Song(resultSet.getInt("id"), resultSet.getString("title"), itemId, resultSet.getInt("user_id"),
                        resultSet.getInt("length"), resultSet.getString("data"), resultSet.getBoolean("burnt")));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return songs;
    }

    public static Song getSong(int songId) {
        Song song = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM soundmachine_songs WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, songId);
            resultSet = preparedStatement.executeQuery();

            // (int id, String title, int itemId, int length, String data, boolean isBurnt)
            if (resultSet.next()) {
                song = new Song(resultSet.getInt("id"), resultSet.getString("title"), songId, resultSet.getInt("user_id"),
                        resultSet.getInt("length"), resultSet.getString("data"), resultSet.getBoolean("burnt"));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return song;
    }

    public static List<SongPlaylist> getSongPlaylist(int itemId) {
        List<SongPlaylist> songs = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM soundmachine_playlists WHERE item_id = ?", sqlConnection);
            preparedStatement.setInt(1, itemId);
            resultSet = preparedStatement.executeQuery();

            // (int id, String title, int itemId, int length, String data, boolean isBurnt)
            while (resultSet.next()) {
                songs.add(new SongPlaylist(itemId, getSong(resultSet.getInt("song_id")), resultSet.getInt("slot_id")));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return songs;
    }

    public static void addPlaylist(int itemId, int songId, int slotId) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO soundmachine_playlists (item_id, song_id, slot_id) VALUES (?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, itemId);
            preparedStatement.setInt(2, songId);
            preparedStatement.setInt(3, slotId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void clearPlaylist(int itemId) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM soundmachine_playlists WHERE item_id = ?", sqlConnection);
            preparedStatement.setInt(1, itemId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }


    public static void addSong(int id, int userId, int soundMachineId, String title, int length, String data) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO soundmachine_songs (id, user_id, item_id, title, length, data) VALUES (?, ?, ?, ?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, soundMachineId);
            preparedStatement.setString(4, title);
            preparedStatement.setInt(5, length);
            preparedStatement.setString(6, data);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }


    public static void addSong(int userId, int soundMachineId, String title, int length, String data) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO soundmachine_songs (user_id, item_id, title, length, data) VALUES (?, ?, ?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, soundMachineId);
            preparedStatement.setString(3, title);
            preparedStatement.setInt(4, length);
            preparedStatement.setString(5, data);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void saveSong(int songId, String title, int length, String data) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE soundmachine_songs SET title = ?, length = ?, data = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, title);
            preparedStatement.setInt(2, length);
            preparedStatement.setString(3, data);
            preparedStatement.setInt(4, songId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void deleteSong(int songId) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement deletePlaylist = null;
        PreparedStatement deleteSong = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();

            // We disable autocommit to make sure the following queries share the same atomic transaction
            sqlConnection.setAutoCommit(false);

            deletePlaylist = Storage.getStorage().prepare("DELETE FROM soundmachine_songs WHERE id = ?", sqlConnection);
            deletePlaylist.setInt(1, songId);
            deletePlaylist.execute();


            deleteSong = Storage.getStorage().prepare("DELETE FROM soundmachine_playlists WHERE song_id = ?", sqlConnection);
            deleteSong.setInt(1, songId);
            deleteSong.execute();

        } catch (Exception e) {
            try {
                // Rollback these queries
                sqlConnection.rollback();
            } catch(SQLException re) {
                Storage.logError(re);
            }

            Storage.logError(e);
        } finally {
            try {
                sqlConnection.setAutoCommit(true);
            } catch (SQLException ce) {
                Storage.logError(ce);
            }

            Storage.closeSilently(deletePlaylist);
            Storage.closeSilently(deleteSong);
            Storage.closeSilently(sqlConnection);
        }
    }


    public static Map<Integer, Integer> getTracks(int soundMachineId) {
        Map<Integer, Integer> tracks = new HashMap<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT track_id, slot_id FROM soundmachine_tracks WHERE soundmachine_id = ?", sqlConnection);
            preparedStatement.setInt(1, soundMachineId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                tracks.put(resultSet.getInt("slot_id"), resultSet.getInt("track_id"));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return tracks;
    }

    public static void addTrack(int soundMachineId, int trackId, int slotId) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO soundmachine_tracks (soundmachine_id, track_id, slot_id) VALUES (?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, soundMachineId);
            preparedStatement.setInt(2, trackId);
            preparedStatement.setInt(3, slotId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void removeTrack(int soundMachineId, int slotId) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM soundmachine_tracks WHERE soundmachine_id = ? AND slot_id = ?", sqlConnection);
            preparedStatement.setInt(1, soundMachineId);
            preparedStatement.setInt(2, slotId);
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
