package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.util.DateUtil;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayerDao {
    private static String figureBlacklist1 = "hd-180-1.hr-100-61.ch-210-66.lg-270-82.sh-290-80";

    public static void resetOnline() {
        try {
            Storage.getStorage().execute("UPDATE users SET is_online = 0 WHERE is_online = 1");
        } catch (SQLException e) {
            Storage.logError(e);
        }
    }
    public static int countIpAddress(String ipAddress) {
        int count = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        PlayerDetails details = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT COUNT(DISTINCT(user_id)) FROM users_ip_logs WHERE ip_address = ?", sqlConnection);
            preparedStatement.setString(1, ipAddress);
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

    /**
     * Logs the IP address for a given user
     *
     * @param userId the user id to edit
     * @param ipAddress the ip address
     */
    public static void logIpAddress(int userId, String ipAddress) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO users_ip_logs (user_id, ip_address) VALUES (?, ?)", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, ipAddress);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Gets the IP address at for a given user
     *
     * @param userId the user id to edit
     */
    public static String getIpAddressAt(int userId, int position, int maxRows) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<String> ipAddresses = new ArrayList<>();

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT ip_address FROM users_ip_logs WHERE user_id = ? ORDER BY created_at DESC LIMIT ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(1, maxRows);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ipAddresses.add(resultSet.getString("ip_address"));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        try {
            return ipAddresses.get(position);
        } catch (Exception ex) {

        }

        return null;
    }

    /**
     * Gets the IP addresses at for a given user
     *
     * @param userId the user id to edit
     */
    public static List<String> getIpAddresses(int userId, int maxRows) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<String> ipAddresses = new ArrayList<>();

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT DISTINCT(ip_address) FROM users_ip_logs WHERE user_id = ? ORDER BY created_at DESC LIMIT ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, maxRows);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ipAddresses.add(resultSet.getString("ip_address"));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return ipAddresses;
    }

    /**
     * Gets the latest IP address for a given user
     *
     * @param userId the user id to edit
     */
    public static String getLatestIp(int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String ip = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT ip_address FROM users_ip_logs WHERE user_id = ? ORDER BY created_at DESC LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                ip = resultSet.getString("ip_address");
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return ip;
    }

    /**
     * Gets the list of random habbos.
     *
     * @param limit the limit of random habbos
     * @return the details
     */
    public static List<PlayerDetails> getRandomHabbos(int limit) {
        List<PlayerDetails> habbos = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users WHERE figure NOT IN ('" + figureBlacklist1 + "') AND UNIX_TIMESTAMP(last_online) > ? ORDER BY RAND() LIMIT ?", sqlConnection);
            preparedStatement.setLong(1, DateUtil.getCurrentTimeSeconds() - TimeUnit.DAYS.toSeconds(30));
            preparedStatement.setInt(2, limit);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                PlayerDetails details = new PlayerDetails();
                fill(details, resultSet);

                habbos.add(details);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return habbos;
    }

    /**
     * Retrieves a list of recent Habbo players ordered by their last online time.
     * If there are fewer users in the database than the specified limit and fillToLimit
     * is true, the method will cycle through all available users repeatedly to fill
     * the list up to the requested limit, ensuring equal distribution.
     *
     * @param limit The maximum number of player details to return. Must be greater than 0.
     * @param fillToLimit If true, will repeat users to reach the specified limit when
     *                    there are fewer users than the limit. If false, will only
     *                    return the actual number of users available.
     * @return A list of PlayerDetails objects containing recent Habbo players.
     *         If fillToLimit is true and there are fewer users than the limit,
     *         users will be repeated equally to fill the list. Returns an empty
     *         list if no users exist in the database or if an error occurs during
     *         database operations.
     *
     * @example
     * // If database contains 3 users and limit is 7:
     * // fillToLimit = true:  Returns [User1, User2, User3, User1, User2, User3, User1]
     * // fillToLimit = false: Returns [User1, User2, User3]
     *
     * @throws No exceptions are thrown - errors are logged via Storage.logError()
     *
     * @since Latest version compatible with MariaDB
     */
    public static List<PlayerDetails> getRecentHabbos(int limit, boolean fillToLimit) {
        List<PlayerDetails> habbos = new ArrayList<>();
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();

            // First, get the count of total users
            PreparedStatement countStatement = Storage.getStorage().prepare(
                    "SELECT COUNT(*) as total_users FROM users", sqlConnection);
            ResultSet countResult = countStatement.executeQuery();

            int totalUsers = 0;
            if (countResult.next()) {
                totalUsers = countResult.getInt("total_users");
            }
            countResult.close();
            countStatement.close();

            if (totalUsers == 0) {
                return habbos; // Return empty list if no users exist
            }

            // Get recent users ordered by last_online
            preparedStatement = Storage.getStorage().prepare(
                    "SELECT * FROM users ORDER BY last_online DESC", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            // Store all users in a temporary list
            List<PlayerDetails> allUsers = new ArrayList<>();
            while (resultSet.next()) {
                PlayerDetails details = new PlayerDetails();
                fill(details, resultSet);
                allUsers.add(details);
            }

            // Fill the result list up to the limit
            if (allUsers.size() >= limit) {
                // If we have enough users, just take the first 'limit' users
                habbos.addAll(allUsers.subList(0, limit));
            } else if (fillToLimit) {
                // If we don't have enough users and fillToLimit is true, repeat them equally
                int usersNeeded = limit;
                int currentIndex = 0;

                while (usersNeeded > 0) {
                    // Add the current user
                    habbos.add(allUsers.get(currentIndex));
                    usersNeeded--;

                    // Move to next user, cycling back to beginning if needed
                    currentIndex = (currentIndex + 1) % allUsers.size();
                }
            } else {
                // If fillToLimit is false, just return the available users without repeating
                habbos.addAll(allUsers);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return habbos;
    }

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
     * @param playerDetails the player details to fill
     * @param username username
     * @param password password
     * @return true, if successful
     */
    public static boolean login(PlayerDetails playerDetails, String username, String password) {
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
                String databasePassword = resultSet.getString("password");

                if (PlayerManager.getInstance().passwordMatches(databasePassword, password)) {
                    fill(playerDetails, resultSet);
                    success = true;
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
     * Login with SSO ticket.
     *
     * @param userId the id of the player to set
     * @param password password
     * @return true, if successful
     */
    public static void setPassword(int userId, String password) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET password = ? WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setString(1, password);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void setEmail(int userId, String email) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET email = ? WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setString(1, email);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Reset SSO ticket
     * Protects against replay attacks
     *
     * @param userId ID of user
     */
    public static void resetSsoTicket(int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET sso_ticket = ? WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setNull(1, Types.VARCHAR);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void resetSsoTickets() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET sso_ticket = ? WHERE sso_ticket IS NOT NULL", sqlConnection);
            preparedStatement.setNull(1, Types.VARCHAR);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Reset SSO ticket
     * Protects against replay attacks
     *
     * @param userId ID of user
     */
    public static void setTicket(int userId, String ticket) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET sso_ticket = ? WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setString(1, ticket);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Reset SSO ticket
     * Protects against replay attacks
     *
     * @param userId ID of user
     */
    public static void setMachineId(int userId, String uniqueId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET machine_id = ? WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setString(1, uniqueId);
            preparedStatement.setInt(2, userId);
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


    public static String getMachineId(int userId) {
        String machineId = "";

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT machine_id FROM users WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                machineId = resultSet.getString("machine_id");
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return machineId;
    }

    public static int countMachineId(String machineId) {
        int count = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT COUNT(*) as users_matched FROM users WHERE machine_id = ? LIMIT 1", sqlConnection);
            preparedStatement.setString(1, machineId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt("users_matched");
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

    public static void saveLastOnline(int userId, long lastOnline, boolean isOnline) {
        //long currentTime = DateUtil.getCurrentTimeSeconds();
        //details.setLastOnline(currentTime);
        java.util.Date date = new Date(lastOnline * 1000L);

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET last_online = ?, is_online = ? WHERE id = ?", sqlConnection);
            preparedStatement.setTimestamp(1, new java.sql.Timestamp(date.getTime()));
            preparedStatement.setInt(2, isOnline ? 1 : 0);
            preparedStatement.setInt(3, userId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void saveSoundSetting(int userId, boolean soundSetting) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET sound_enabled = ? WHERE id = ?", sqlConnection);
            preparedStatement.setBoolean(1, soundSetting);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void saveDetails(int userId, String figure, String poolFigure, String sex) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET figure = ?, pool_figure = ?, sex = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, figure);
            preparedStatement.setString(2, poolFigure);
            preparedStatement.setString(3, sex);
            preparedStatement.setInt(4, userId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void saveMotto(int userId, String motto) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET motto = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, motto);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void saveCurrency(int userId, int credits) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET credits = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, credits);
            preparedStatement.setInt(3, userId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void saveEmail(int userId, String email) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET email = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, email);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }


    public static void saveSelectedRoom(int userId, int selectedRoom) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET selected_room_id = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, selectedRoom);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void saveRespect(int userId, int dailyRespectPoints, int respectPoints, String respectDay, int respectGiven) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET daily_respect_points = ?, respect_points = ?, respect_day = ?, respect_given = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, dailyRespectPoints);
            preparedStatement.setInt(2, respectPoints);
            preparedStatement.setString(3, respectDay);
            preparedStatement.setInt(4, respectGiven);
            preparedStatement.setInt(5, userId);
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
     */
    public static void saveSubscription(int userId, long firstClubSubscription, long clubExpiration) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET club_subscribed = ?, club_expiration = ? WHERE id = ?", sqlConnection);
            preparedStatement.setLong(1, firstClubSubscription);
            preparedStatement.setLong(2, clubExpiration);
            preparedStatement.setInt(3, userId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Update xp.
     *
     * @param userId the id of the player to save
     */
    public static void saveOnlineStatus(int userId, boolean onlineStatusVisible) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET online_status_visible = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, onlineStatusVisible ? 1 : 0);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Save favourite group
     *
     * @param userId the id of the user to save to
     * @param groupId the favourite group id
     */
    public static void saveFavouriteGroup(int userId, int groupId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET favourite_group = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Gets the home room of user.
     *
     * @param id the user id
     * @return the home room id
     */
    public static int getHomeRoom(int id) {
        int roomId = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT home_room FROM users WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                roomId = resultSet.getInt("home_room");
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
     * Save favourite group
     *
     * @param userId the id of the user to save to
     * @param roomId the room id of the users home
     */
    public static void saveHomeRoom(int userId, int roomId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET home_room = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, roomId);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static boolean isPlayerOnline(int userId) {
        boolean isOnline = false;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT is_online FROM users WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                isOnline = resultSet.getBoolean("is_online");
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return isOnline;
    }

    public static int getByEmail(String email) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        int id = -1;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users WHERE email = ? LIMIT 1", sqlConnection);
            preparedStatement.setString(1, email);
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

    public static void savePassword(int userId, String password) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET password = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, password);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void saveBirthday(int userId, String birthday) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET birthday = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, birthday);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Register user
     */
    public static void register(String username, String password, String figure, String sex, String email, String birthday){
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Storage.getStorage().getConnection();
            stmt = Storage.getStorage().prepare("INSERT INTO users (username, password, figure, sex, pool_figure, sso_ticket, email, birthday) VALUES (?, ?, ?, ?, '', '', ?, ?)", conn);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, figure);
            stmt.setString(4, sex);
            stmt.setString(5, email);
            stmt.setString(6, birthday);
            stmt.execute();
        } catch (SQLException e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(stmt);
            Storage.closeSilently(conn);
        }
    }

    public static void saveReceiveMail(PlayerDetails details) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET receive_email = ? WHERE id = ?", sqlConnection);
            preparedStatement.setBoolean(1, details.isReceiveNews());
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
     * Fill player data
     *
     * @param details the details
     * @param row the row
     * @throws SQLException the SQL exception
     */
    public static void fill(PlayerDetails details, ResultSet row) throws SQLException {
        // public void fill(int id, String username, String password, String figure, String poolFigure, int credits, String motto, String consoleMotto, String sex,
        // int tickets, int film, int rank, long lastOnline, long clubSubscribed, long clubExpiration, String badge, String badgeActive) {
        if (details == null) {
            return;
        }

        details.fill(
                row.getInt("id"), row.getString("username"), row.getString("figure"),
                row.getString("pool_figure"), row.getInt("credits"),
                row.getString("email"), row.getString("console_motto"), row.getString("motto"), row.getString("sex"),
                row.getString("sso_ticket"), row.getInt("tickets"), row.getInt("film"),
                row.getInt("rank"), row.getTime("last_online").getTime() / 1000L,
                row.getTime("created_at").getTime() / 1000L, row.getString("machine_id"),
                row.getLong("club_subscribed"), row.getLong("club_expiration"),
                row.getBoolean("allow_stalking"), row.getInt("selected_room_id"),
                row.getBoolean("allow_friend_requests"), row.getBoolean("online_status_visible"),
                row.getBoolean("profile_visible"), row.getBoolean("wordfilter_enabled"),
                row.getBoolean("trade_enabled"), row.getBoolean("sound_enabled"),
                row.getLong("trade_ban_expiration"), row.getBoolean("receive_email"),
                row.getBoolean("is_online"), row.getInt("favourite_group"),
                row.getString("created_at")
        );
    }

}
