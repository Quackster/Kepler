package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.events.Event;
import org.alexdev.kepler.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventsDao {
    public static void addEvent(int roomId, int userId, int categoryId, String name, String description, long expireTime) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO rooms_events (room_id, user_id, category_id, name, description, expire_time) VALUES (?, ?, ?, ?, ?, ?)", sqlConnection);

            preparedStatement.setInt(1, roomId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, categoryId);
            preparedStatement.setString(4, name);
            preparedStatement.setString(5, description);
            preparedStatement.setLong(6, expireTime);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void removeEvent(Event event) {
        removeEvents(List.of(event));
    }


    public static void removeEvents(List<Event> eventList) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM rooms_events WHERE room_id = ?", sqlConnection);
            sqlConnection.setAutoCommit(false);

            for (Event event : eventList) {
                preparedStatement.setInt(1, event.getRoomId());
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

    public static void removeExpiredEvents() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM rooms_events WHERE expire_time < ?", sqlConnection);
            preparedStatement.setLong(1, DateUtil.getCurrentTimeSeconds());
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static List<Event> getEvents() {
        List<Event> eventMap = new CopyOnWriteArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM rooms_events WHERE expire_time > ?", sqlConnection);
            preparedStatement.setLong(1, DateUtil.getCurrentTimeSeconds());
            resultSet = preparedStatement.executeQuery();

            //Event(int roomId, int userId, int categoryId, String name, String description, long started)
            while (resultSet.next()) {
                eventMap.add(new Event(
                        resultSet.getInt("room_id"), resultSet.getInt("user_id"),
                        resultSet.getInt("category_id"), resultSet.getString("name"),
                        resultSet.getString("description"), resultSet.getLong("expire_time")));
            }
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return eventMap;
    }

    public static void save(Event event) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE rooms_events SET category_id = ?, name = ?, description = ? WHERE room_id = ?", sqlConnection);
            preparedStatement.setInt(1, event.getCategoryId());
            preparedStatement.setString(2, event.getName());
            preparedStatement.setString(3, event.getDescription());
            preparedStatement.setInt(4, event.getRoomId());
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}
