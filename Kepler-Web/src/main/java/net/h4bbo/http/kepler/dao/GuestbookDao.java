package net.h4bbo.http.kepler.dao;

import net.h4bbo.kepler.dao.Storage;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.http.kepler.game.homes.GuestbookEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GuestbookDao {
    public static GuestbookEntry create(int userId, int homeId, int groupId, String message) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        int guestbookId = -1;
        long guestbookCreation = -1;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO cms_guestbook_entries (user_id, home_id, group_id, message) VALUES (?, ?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, homeId);
            preparedStatement.setInt(3, groupId);
            preparedStatement.setString(4, message);
            preparedStatement.execute();
            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                guestbookId = resultSet.getInt("id");
                guestbookCreation = DateUtil.getCurrentTimeSeconds();
            }


        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(preparedStatement);
        }

        return new GuestbookEntry(guestbookId, userId, homeId, groupId, message, guestbookCreation);
    }

    public static void remove(int id, int homeId, int groupId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM cms_guestbook_entries WHERE id = ? AND home_id = ? AND group_id = ?", sqlConnection);
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, homeId);
            preparedStatement.setInt(3, groupId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(preparedStatement);
        }
    }

    public static GuestbookEntry getEntry(int id) {
        GuestbookEntry entry = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_guestbook_entries WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, id);
            resultSet =  preparedStatement.executeQuery();

            while (resultSet.next()) {
                entry = fill(resultSet);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return entry;
    }

    public static List<GuestbookEntry> getEntriesByHome(int homeId) {
        List<GuestbookEntry> entries = new ArrayList<GuestbookEntry>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_guestbook_entries WHERE home_id = ? ORDER BY created_at DESC LIMIT 500", sqlConnection);
            preparedStatement.setInt(1, homeId);
            resultSet =  preparedStatement.executeQuery();

            while (resultSet.next()) {
                entries.add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return entries;
    }

    public static List<GuestbookEntry> getEntriesByGroup(int groupId) {
        List<GuestbookEntry> entries = new ArrayList<GuestbookEntry>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_guestbook_entries WHERE group_id = ? ORDER BY created_at DESC LIMIT 500", sqlConnection);
            preparedStatement.setInt(1, groupId);
            resultSet =  preparedStatement.executeQuery();

            while (resultSet.next()) {
                entries.add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return entries;
    }

    private static GuestbookEntry fill(ResultSet resultSet) throws SQLException {
        return new GuestbookEntry(resultSet.getInt("id"), resultSet.getInt("user_id"), resultSet.getInt("home_id"), resultSet.getInt("group_id"),
                resultSet.getString("message"), resultSet.getTime("created_at").getTime() / 1000L);
    }
}
