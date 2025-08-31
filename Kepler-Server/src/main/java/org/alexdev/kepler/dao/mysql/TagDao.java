package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.tags.HabboTag;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class TagDao {
    public static List<HabboTag> getTagInfoList(String tag) {
        List<HabboTag> search = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users_tags WHERE LOWER(tag) = ? AND (user_id > 0 OR group_id > 0) AND room_id = 0", sqlConnection);
            preparedStatement.setString(1, tag);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                search.add(new HabboTag(resultSet.getString("tag"), resultSet.getInt("room_id"), resultSet.getInt("user_id"), resultSet.getInt("group_id")));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return search;
    }

    public static List<Room> querySearchRooms(String searchTag) {
        List<Room> rooms = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users_tags " +
                    "INNER JOIN rooms ON rooms.id = users_tags.room_id " +
                    "INNER JOIN users ON rooms.owner_id = users.id  " +
                    "WHERE LOWER(users_tags.tag) LIKE ? LIMIT 30", sqlConnection);
            preparedStatement.setString(1, "%" + searchTag + "%");
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

    public static List<Pair<String, Integer>> getPopularTags() {
        return getPopularTags(20);
    }

    public static List<Pair<String, Integer>> getPopularTags(int num) {
        List<Pair<String, Integer>> tagList = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT tag, COUNT(*) AS quantity FROM users_tags GROUP BY tag ORDER BY quantity DESC LIMIT " + num, sqlConnection);
            resultSet = preparedStatement.executeQuery();

            int tags = 0;
            Map<String, Integer> temp = new HashMap<>();

            while (resultSet.next()) {
                String tag = resultSet.getString("tag");
                int count = resultSet.getInt("quantity");

                if (temp.containsKey(tag)) {
                    count = temp.get(tag) + count;
                }

                tags += count;
                temp.put(tag, count);
            }

            List<Map.Entry<String, Integer> > list = new LinkedList<>(temp.entrySet());
            list.sort(Comparator.comparingInt(Map.Entry::getValue));


            int weight = 0;
            int[] fonts = new int[] { 10, 12, 14, 20 };//20, 14, 12, 10 };//10, 12, 14, 20};

            if (temp.size() > 0) {
                int counter = temp.size();
                int bits = (int) Math.ceil(temp.size() / 4);

                if (tags > 0) {
                    for (var kvp : list) {
                        weight = 0;

                        if (counter == (bits)) {
                            weight = 3;
                        }

                        if (counter == (bits * 3)) {
                            weight = 2;
                        }

                        if (counter == (bits * 2)) {
                            weight = 1;
                        }

                        String key = kvp.getKey();

                        tagList.add(Pair.of(key, fonts[weight]));
                        counter--;
                    }
                }
            }
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        Collections.shuffle(tagList);
        return tagList;
    }

    public static List<String> getUserTags(int userId) {
        List<String> roomIds = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT tag FROM users_tags WHERE user_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                roomIds.add(resultSet.getString("tag"));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return roomIds;
    }

    public static List<String> getRoomTags(int roomId) {
        List<String> roomIds = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT tag FROM users_tags WHERE room_id = ?", sqlConnection);
            preparedStatement.setInt(1, roomId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                roomIds.add(resultSet.getString("tag"));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return roomIds;
    }

    public static List<String> getGroupTags(int groupId) {
        List<String> roomIds = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT tag FROM users_tags WHERE group_id = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                roomIds.add(resultSet.getString("tag"));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return roomIds;
    }

    public static Map<String, Integer> getRoomTagData(int num) {
        Map<String, Integer> tagList = new HashMap<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT tag, COUNT(user_id) AS quantity FROM users_tags WHERE room_id > 0 GROUP BY tag ORDER BY quantity DESC LIMIT " + num, sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String tag = resultSet.getString("tag");
                int count = resultSet.getInt("quantity");

                tagList.put(tag, count);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return tagList;
    }

    public static boolean hasTag(int userId, int roomId, int groupId, String tag) {
        boolean result = false;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT tag FROM users_tags WHERE user_id = ? AND room_id = ? AND group_id = ? AND LOWER(tag) = LOWER(?)", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, roomId);
            preparedStatement.setInt(3, groupId);
            preparedStatement.setString(4, tag);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                result = true;
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return result;
    }

    public static void addTag(int userId, int roomId, int groupId, String tag) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO users_tags (user_id, room_id, group_id, tag) VALUES (?, ?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, roomId);
            preparedStatement.setInt(3, groupId);
            preparedStatement.setString(4, tag);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void removeTag(int userId, int roomId, int groupId, String tag) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM users_tags WHERE user_id = ? AND room_id = ? AND group_id = ? AND tag = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, roomId);
            preparedStatement.setInt(3, groupId);
            preparedStatement.setString(4, tag);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void removeTags(int userId, int roomId, int groupId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM users_tags WHERE user_id = ? AND room_id = ? AND group_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, roomId);
            preparedStatement.setInt(3, groupId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static int countTag(String tag) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        int count = 0;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT COUNT(user_id) FROM users_tags WHERE tag = ?", sqlConnection);
            preparedStatement.setString(1, tag);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return count;
    }
}
