package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.player.PlayerDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BadgeDao {
    /**
     * Update current badge
     *
     * @param details the player details to save
     */
    public static void saveCurrentBadge(PlayerDetails details) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET badge = ?, badge_active = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, details.getCurrentBadge());
            preparedStatement.setBoolean(2, details.getShowBadge());
            preparedStatement.setInt(3, details.getId());
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Add new badge to user
     *
     * @param userId
     * @param badge
     */
    public static void addBadge(int userId, String badge) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Storage.getStorage().getConnection();
            stmt = Storage.getStorage().prepare("INSERT INTO users_badges (user_id, badge) VALUES (?, ?)", conn);
            stmt.setInt(1, userId);
            stmt.setString(2, badge);
            stmt.execute();
        } catch (SQLException e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(stmt);
            Storage.closeSilently(conn);
        }
    }

    /**
     * Get all rank badges
     *
     * @return list of badges
     */
    public static List<String> getAllRankBadges() {
        List<String> badges = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet row = null;

        // TODO: merge two queries somehow
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

    /**
     * Get badges for user, including those inherited from rank
     *
     * @param userId
     * @return list of badges
     */
    public static List<String> getBadges(int userId) {
        List<String> badges = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement stmt2 = null;
        ResultSet row = null;
        ResultSet row2 = null;

        // TODO: merge two queries somehow
        try {
            conn = Storage.getStorage().getConnection();

            stmt = Storage.getStorage().prepare("SELECT badge FROM users_badges WHERE user_id = ?", conn);
            stmt.setInt(1, userId);
            row = stmt.executeQuery();

            stmt2 = Storage.getStorage().prepare("SELECT rank_badges.badge FROM rank_badges LEFT JOIN users ON rank_badges.rank <= users.rank WHERE users.id = ?", conn);
            stmt2.setInt(1, userId);
            row2 = stmt2.executeQuery();

            while (row.next()) {
                badges.add(row.getString("badge"));
            }

            while (row2.next()) {
                badges.add(row2.getString("badge"));
            }

        } catch (Exception err) {
            Storage.logError(err);
        } finally {
            Storage.closeSilently(row);
            Storage.closeSilently(row2);
            Storage.closeSilently(stmt);
            Storage.closeSilently(stmt2);
            Storage.closeSilently(conn);
        }

        return badges;
    }
}
