package org.alexdev.kepler.dao.mysql;


import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.badges.Badge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BadgeDao {
    public static Map<Integer, List<String>> getRoomBadges()  {
        Map<Integer, List<String>> badges = new ConcurrentHashMap<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM rooms_entry_badges", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int roomId = resultSet.getInt("room_id");
                String badgeCode = resultSet.getString("badge");

                if (!badges.containsKey(roomId)) {
                    badges.put(roomId, new ArrayList<>());
                }

                badges.get(roomId).add(badgeCode);
            }
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return badges;
    }


    public static void deleteRoomBadge(String roomId, String badgeCode) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM rooms_entry_badges WHERE room_id = ? AND badge = ?", sqlConnection);
            preparedStatement.setString(1, roomId);
            preparedStatement.setString(2, badgeCode);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void createEntryBadge(int roomId, String badgeCode) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO rooms_entry_badges (room_id, badge) VALUES (?, ?)", sqlConnection);
            preparedStatement.setInt(1, roomId);
            preparedStatement.setString(2, badgeCode);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void updateBadges(Map<Integer, List<String>> badges) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();

            preparedStatement = Storage.getStorage().prepare("DELETE FROM rooms_entry_badges", sqlConnection);
            preparedStatement.execute();

            preparedStatement = Storage.getStorage().prepare("INSERT INTO rooms_entry_badges (room_id, badge) VALUES (?, ?)", sqlConnection);
            sqlConnection.setAutoCommit(false);

            for (var badgeData : badges.entrySet()) {
                for (var badge : badgeData.getValue()) {
                    preparedStatement.setInt(1, badgeData.getKey());
                    preparedStatement.setString(2, badge);
                    preparedStatement.addBatch();
                }
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

    public static List<Badge> getBadges(int userId) {
        List<Badge> ranks = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users_badges WHERE user_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ranks.add(new Badge(resultSet.getString("badge"), resultSet.getBoolean("equipped"), resultSet.getInt("slot_id")));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return ranks;
    }

    public static void newBadge(int userId, String badgeCode) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO users_badges (user_id, badge) VALUES (?, ?)", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, badgeCode);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void removeBadge(int userId, String badgeCode) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM users_badges WHERE user_id = ? AND badge = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, badgeCode);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void removeBadge(String badgeCode) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM users_badges WHERE badge = ?", sqlConnection);
            preparedStatement.setString(1, badgeCode);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void saveBadgeChanges(int userId, String badgeCode, boolean isEquipped, int slotId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users_badges SET equipped = ?, slot_id = ? WHERE user_id = ? AND badge = ?", sqlConnection);
            preparedStatement.setBoolean(1, isEquipped);
            preparedStatement.setInt(2, slotId);
            preparedStatement.setInt(3, userId);
            preparedStatement.setString(4, badgeCode);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }


    public static void saveBadgeChanges(int userId, Set<Badge> badgesToSave) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users_badges SET equipped = ?, slot_id = ? WHERE user_id = ? AND badge = ?", sqlConnection);
            sqlConnection.setAutoCommit(false);

            for (Badge badge : badgesToSave) {
                preparedStatement.setBoolean(1, badge.isEquipped());
                preparedStatement.setInt(2, badge.getSlotId());
                preparedStatement.setInt(3, userId);
                preparedStatement.setString(4, badge.getBadgeCode());
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
     * Get all rank badges
     *
     * @return list of badges
     */
    public static List<String> getRankBadges() {
        List<String> badges = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet row = null;

        try {
            conn = Storage.getStorage().getConnection();
            stmt = Storage.getStorage().prepare("SELECT badge FROM rank_badges", conn);
            row = stmt.executeQuery();

            while (row.next()) {
                badges.add(row.getString("badge"));
            }
        } catch (Exception err) {
            Storage.logError(err);
        } finally {
            Storage.closeSilently(row);
            Storage.closeSilently(stmt);
            Storage.closeSilently(conn);
        }

        return badges;
    }
}
