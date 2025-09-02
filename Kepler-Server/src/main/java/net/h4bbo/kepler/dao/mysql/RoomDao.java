package net.h4bbo.kepler.dao.mysql;

import net.h4bbo.kepler.dao.Storage;
import net.h4bbo.kepler.game.moderation.ChatMessage;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.RoomData;
import net.h4bbo.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import net.h4bbo.kepler.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDao {

    public static void resetVisitors() {
        try {
            Storage.getStorage().execute("UPDATE rooms SET visitors_now = 0 WHERE visitors_now > 0");
        } catch (SQLException e) {
            Storage.logError(e);
        }
    }

    /**
     * Get a list of rooms by the owner id, use "0" for public rooms.
     *
     * @param userId the user id to get the rooms by
     * @return the list of rooms
     */
    public static List<Room> getRoomsByUserId(int userId) {
        List<Room> rooms = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM rooms LEFT JOIN users ON rooms.owner_id = users.id WHERE rooms.owner_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Room room = new Room();
                fill(room.getData(), resultSet);
                rooms.add(room);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return rooms;
    }

    public static void saveChatLog(List<ChatMessage> chatMessageList) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO room_chatlogs (user_id, room_id, timestamp, chat_type, message) VALUES (?, ?, ?, ?, ?)", sqlConnection);
            sqlConnection.setAutoCommit(false);

            for (ChatMessage chatMessage : chatMessageList) {
                preparedStatement.setInt(1, chatMessage.getPlayerId());
                preparedStatement.setInt(2, chatMessage.getRoomId());
                preparedStatement.setLong(3, DateUtil.getCurrentTimeSeconds());

                switch (chatMessage.getChatMessageType()) {
                    case CHAT:
                        preparedStatement.setInt(4, 0);
                        break;
                    case SHOUT:
                        preparedStatement.setInt(4, 1);
                        break;
                    default:
                        preparedStatement.setInt(4, 2);
                        break;
                }

                preparedStatement.setString(5, chatMessage.getMessage());
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

    /**
     * Save rating for the room
     *
     * @param roomId the room to save
     * @param rating the new rating
     */
    public static void saveRating(int roomId, int rating) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE rooms SET rating = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, rating);
            preparedStatement.setInt(2, roomId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Get a list of recommended rooms.
     *
     * @param limit the limit of rooms
     * @return the list of rooms
     */
    public static List<Room> getRecommendedRooms(int limit, int offset) {
        List<Room> roomList = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM rooms LEFT JOIN users ON rooms.owner_id = users.id WHERE owner_id > 0 AND accesstype = 0 ORDER BY visitors_now DESC, rating DESC LIMIT " + limit + " OFFSET " + offset, sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Room room = new Room();
                RoomDao.fill(room.getData(), resultSet);
                roomList.add(room);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return roomList;
    }

    /**
     * Get a list of random rooms.
     *
     * @param limit the limit of rooms
     * @return the list of rooms
     */
    public static List<Room> getHighestRatedRooms(int limit, int offset) {
        List<Room> rooms = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM rooms LEFT JOIN users ON rooms.owner_id = users.id WHERE owner_id > 0 ORDER BY rating DESC LIMIT " + limit + " OFFSET " + offset, sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Room room = new Room();
                RoomDao.fill(room.getData(), resultSet);
                rooms.add(room);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return rooms;
    }

    /**
     * Get the room id of a room by its model, used for walkways.
     *
     * @param model the model used to get the id for
     * @return the id, else -1
     */
    public static int getRoomIdByModel(String model) {
        int roomId = -1;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT id FROM rooms WHERE model = ?", sqlConnection);
            preparedStatement.setString(1, model);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                roomId = resultSet.getInt("id");
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return roomId;
    }


    /**
     * Search query for when people use the navigator search, will search either by username or room name similarities.
     *
     * @param searchQuery the query to use
     * @return the list of possible room matches
     */
    public static List<Room> querySearchRooms(String searchQuery) {
        List<Room> rooms = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM rooms INNER JOIN users ON rooms.owner_id = users.id WHERE LOWER(users.username) LIKE ? OR LOWER(rooms.name) LIKE ? LIMIT 30", sqlConnection);
            preparedStatement.setString(1, "%" + searchQuery + "%");
            preparedStatement.setString(2, "%" + searchQuery + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Room room = new Room();
                fill(room.getData(), resultSet);
                rooms.add(room);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return rooms;
    }

    public static Room getRoomById(int roomId) {
        Room room = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM rooms LEFT JOIN users ON rooms.owner_id = users.id WHERE rooms.id = ?", sqlConnection);
            preparedStatement.setInt(1, roomId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                room = new Room();
                fill(room.getData(), resultSet);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return room;
    }

    /**
     * Save all room information.
     *
     * @param room the room to save
     */
    public static void saveDecorations(Room room) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE rooms SET wallpaper = ?, floor = ?, landscape = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, room.getData().getWallpaper());
            preparedStatement.setInt(2, room.getData().getFloor());
            preparedStatement.setString(3, room.getData().getLandscape());
            preparedStatement.setInt(4, room.getId());
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Save all room information.
     *
     * @param room the room to save
     */
    public static void save(Room room) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE rooms SET category = ?, name = ?, description = ?, showname = ?, superusers = ?, accesstype = ?, password = ?, visitors_max = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, room.getData().getCategoryId());
            preparedStatement.setString(2, room.getData().getName());
            preparedStatement.setString(3, room.getData().getDescription());
            preparedStatement.setBoolean(4, room.getData().showOwnerName());
            preparedStatement.setBoolean(5, room.getData().allowSuperUsers());
            preparedStatement.setInt(6, room.getData().getAccessTypeId());
            preparedStatement.setString(7, room.getData().getPassword());
            preparedStatement.setInt(8, room.getData().getVisitorsMax());
            preparedStatement.setInt(9, room.getId());
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }


    /**
     * Save visitor count of rooms
     *
     * @param room the room to save
     */
    public static void saveVisitors(Room room) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE rooms SET visitors_now = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, room.getData().getVisitorsNow());
            preparedStatement.setInt(2, room.getId());
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }


    /**
     * Delete room.
     *
     * @param room the room to delete
     */
    public static void delete(Room room) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM rooms WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, room.getId());
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Search query for when people use the navigator search, will search either by username or room name similarities.
     *
     * @param searchQuery the query to use
     * @return the list of possible room matches
     */
    public static List<Room> searchRooms(String searchQuery, int roomOwner, int limit) {
        List<Room> rooms = new ArrayList<>();

        if (searchQuery.isBlank() && roomOwner == -1) {
            return rooms;
        }

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM rooms INNER JOIN users ON rooms.owner_id = users.id WHERE" + (roomOwner > 0 ? (" owner_id = " + roomOwner + " AND") : " LOWER(users.username) LIKE ? OR") + " LOWER(rooms.name) LIKE ? ORDER BY visitors_now DESC, rating DESC LIMIT ? ", sqlConnection);

            if (roomOwner > 0) {
                preparedStatement.setString(1, "%" + searchQuery + "%");
                preparedStatement.setInt(2, limit);
            } else {
                preparedStatement.setString(1, "%" + searchQuery + "%");
                preparedStatement.setString(2, "%" + searchQuery + "%");
                preparedStatement.setInt(3, limit);
            }

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Room room = new Room();
                fill(room.getData(), resultSet);
                rooms.add(room);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return rooms;
    }

    /**
     * Save visitor count of rooms
     *
     * @param roomId the room to save
     */
    public static void saveGroupId(int roomId, int groupId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE rooms SET group_id = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, roomId);
            preparedStatement.setInt(2, groupId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Fill room data
     *
     * @param data the room data instance
     * @param row  the row
     * @throws SQLException the SQL exception
     */
    public static void fill(RoomData data, ResultSet row) throws SQLException {
        if (data == null) {
            return;
        }

        String ownerName = row.getString("username");

        //     public void fill(int id, int ownerId, String ownerName, int category, String name, String description, String model, String ccts, int wallpaper, int floor, String decoration, boolean showName, boolean superUsers, int accessType,
        //     String password, int visitorsNow, int visitorsMax, int rating, boolean isHidden) {
        data.fill(row.getInt("id"), row.getInt("owner_id"), ownerName != null ? ownerName : "", row.getInt("category"),
                row.getString("name"), row.getString("description"), row.getString("model"),
                row.getString("ccts"), row.getInt("wallpaper"), row.getInt("floor"), row.getString("landscape"), row.getBoolean("showname"),
                row.getBoolean("superusers"), row.getInt("accesstype"), row.getString("password"),
                row.getInt("visitors_now"), row.getInt("visitors_max"), row.getInt("rating"),
                row.getInt("group_id"), row.getBoolean("is_hidden"));

    }
}
