package org.alexdev.kepler.dao.mysql;

import com.goterl.lazycode.lazysodium.LazySodiumJava;
import com.goterl.lazycode.lazysodium.SodiumJava;
import com.goterl.lazycode.lazysodium.interfaces.PwHash;
import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.util.DateUtil;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerDao {
    public static final LazySodiumJava LIB_SODIUM = new LazySodiumJava(new SodiumJava());

    /**
     * Gets the details by user id
     *
     * @param userId the user id
     * @return the details
     */
    public static PlayerDetails getDetails(int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        PlayerDetails details = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                details = new PlayerDetails();
                fill(details, resultSet);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return details;
    }

    /**
     * Gets the details by username
     *
     * @param username the username
     * @return the details
     */
    public static PlayerDetails getDetails(String username) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        PlayerDetails details = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users WHERE username = ? LIMIT 1", sqlConnection);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                details = new PlayerDetails();
                fill(details, resultSet);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return details;
    }

    /**
     * Login with SSO ticket.
     *
     * @param player the player
     * @param ssoTicket the sso ticket
     * @return true, if successful
     */
    public static boolean loginTicket(Player player, String ssoTicket) {
        boolean success = false;
        
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users WHERE sso_ticket = ? LIMIT 1", sqlConnection);
            preparedStatement.setString(1, ssoTicket);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                fill(player.getDetails(), resultSet);
                success = true;
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return success;
    }

    /**
     * Login with SSO ticket.
     *
     * @param player the player
     * @param username username
     * @param password password
     * @return true, if successful
     */
    public static boolean login(Player player, String username, String password) {
        boolean success = false;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users WHERE username = ? LIMIT 1", sqlConnection);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                byte[] hashedPassword = (resultSet.getString("password") + '\0').getBytes(StandardCharsets.UTF_8);
                byte[] pass = password.getBytes(StandardCharsets.UTF_8);

                PwHash.Native pwHash = (PwHash.Native) LIB_SODIUM;
                success = pwHash.cryptoPwHashStrVerify(hashedPassword, pass, pass.length);

                if (success) {
                    fill(player.getDetails(), resultSet);
                }
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return success;
    }

    /**
     * Clear SSO ticket
     * Protects against replay attacks
     *
     * @param userId ID of user
     */
    public static void clearSSOTicket(int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET sso_ticket = '' WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Gets the id.
     *
     * @param username the username
     * @return the id
     */
    public static int getId(String username) {
        int id = -1;
        
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT id FROM users WHERE LOWER(username) = ? LIMIT 1", sqlConnection);
            preparedStatement.setString(1, username.toLowerCase());
            resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return id;    
    }
    
    /**
     * Gets the name.
     *
     * @param id the id
     * @return the name
     */
    public static String getName(int id) {
        String name = null;
        
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT username FROM users WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                name = resultSet.getString("username");
            }
            
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return name;    
    }

    /**
     * Register user
     */
    public static void register(String username, String password, String figure, String sex){
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Storage.getStorage().getConnection();
            stmt = Storage.getStorage().prepare("INSERT INTO users (username, password, figure, sex, pool_figure, sso_ticket) VALUES (?, ?, ?, ?, '', '')", conn);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, figure);
            stmt.setString(4, sex);
            stmt.execute();
        } catch (SQLException e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(stmt);
            Storage.closeSilently(conn);
        }
    }

    /**
     * Update last online.
     *
     * @param details the details of the user
     */
    public static void saveLastOnline(PlayerDetails details) {
        long currentTime = DateUtil.getCurrentTimeSeconds();
        details.setLastOnline(currentTime);

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET last_online = ? WHERE id = ?", sqlConnection);
            preparedStatement.setLong(1, currentTime);
            preparedStatement.setInt(2, details.getId());
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Update sound setting.
     *
     * @param details the details of the user
     */
    public static void saveSoundSetting(PlayerDetails details) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET sound_enabled = ? WHERE id = ?", sqlConnection);
            preparedStatement.setBoolean(1, details.getSoundSetting());
            preparedStatement.setInt(2, details.getId());
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Update details.
     *
     * @param details the player details to save
     */
    public static void saveDetails(PlayerDetails details) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET figure = ?, pool_figure = ?, sex = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, details.getFigure());
            preparedStatement.setString(2, details.getPoolFigure());
            preparedStatement.setString(3, Character.toString(details.getSex()));
            preparedStatement.setInt(4, details.getId());
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Update details.
     *
     * @param details the player details to save
     */
    public static void saveMotto(PlayerDetails details) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET motto = ?, console_motto = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, details.getMotto());
            preparedStatement.setString(2, details.getConsoleMotto());
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
     * Update details.
     *
     * @param details the player details to save
     */
    public static void saveSubscription(PlayerDetails details) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET club_subscribed = ?, club_expiration = ? WHERE id = ?", sqlConnection);
            preparedStatement.setLong(1, details.getFirstClubSubscription());
            preparedStatement.setLong(2, details.getClubExpiration());
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

    /**
     * Fill player data
     *
     * @param details the details
     * @param row the row
     * @throws SQLException the SQL exception
     */
    private static void fill(PlayerDetails details, ResultSet row) throws SQLException {
        // public void fill(int id, String username, String password, String figure, String poolFigure, int credits, String motto, String consoleMotto, String sex,
        // int tickets, int film, int rank, long lastOnline, long clubSubscribed, long clubExpiration, String badge, String badgeActive) {
        if (details == null) {
            return;
        }

        details.fill(row.getInt("id"), row.getString("username"), row.getString("figure"),
                row.getString("pool_figure"), row.getInt("credits"), row.getString("motto"),
                row.getString("console_motto"), row.getString("sex"), row.getInt("tickets"),
                row.getInt("film"), row.getInt("rank"), row.getLong("last_online"),
                row.getLong("club_subscribed"), row.getLong("club_expiration"), row.getLong("club_gift_due"),
                row.getString("badge"),
                row.getBoolean("badge_active"), row.getBoolean("allow_stalking"),
                row.getBoolean("allow_friend_requests"), row.getBoolean("sound_enabled"),
                row.getBoolean("tutorial_finished"), row.getInt("battleball_points"),
                row.getInt("snowstorm_points"));
    }
}
