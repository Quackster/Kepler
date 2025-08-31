package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.player.statistics.PlayerStatistic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class PlayerStatisticsDao {
    public static void updateStatistic(int userId, PlayerStatistic statistic, String value) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users_statistics SET " + statistic.getColumn() + " = ? WHERE user_id = ?", sqlConnection);

            if (statistic.isDateTime()) {
                preparedStatement.setTimestamp(1, new Timestamp(Long.parseLong(value) * 1000L));
            } else {
                preparedStatement.setString(1, value);
            }

            preparedStatement.setInt(2, userId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void updateStatistic(int userId, PlayerStatistic statistic, int value) {
        updateStatistic(userId, statistic, String.valueOf(value));
    }

    public static void updateStatistic(int userId, PlayerStatistic statistic, long value) {
        updateStatistic(userId, statistic, String.valueOf(value));
    }


    public static void incrementStatistic(int userId, PlayerStatistic statistic, long value) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users_statistics SET " + statistic.getColumn() + " = " + statistic.getColumn() + " + ? WHERE user_id = ?", sqlConnection);
            preparedStatement.setLong(1, value);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void updateStatistics(int userId, HashMap<PlayerStatistic, String> statisticMap) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();

            for (var kvp : statisticMap.entrySet()) {
                var statistic = kvp.getKey();
                var value = kvp.getValue();
                preparedStatement = Storage.getStorage().prepare("UPDATE users_statistics SET " + statistic.getColumn() + " = ? WHERE user_id = ?", sqlConnection);

                if (statistic.isDateTime()) {
                    preparedStatement.setTimestamp(5, new Timestamp(Long.parseLong(value) * 1000L));
                } else {
                    preparedStatement.setString(1, value);
                }

                preparedStatement.setInt(2, userId);
                preparedStatement.execute();
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void incrementStatistics(int userId, HashMap<PlayerStatistic, Long> statisticMap) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();

            for (var kvp : statisticMap.entrySet()) {
                var statistic = kvp.getKey();
                long value = kvp.getValue();
                preparedStatement = Storage.getStorage().prepare("UPDATE users_statistics SET " + statistic.getColumn() + " = " + statistic.getColumn() + " + ? WHERE user_id = ?", sqlConnection);
                preparedStatement.setLong(1, value);
                preparedStatement.setInt(2, userId);
                preparedStatement.execute();
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static long getStatisticLong(int userId, PlayerStatistic playerStatistic) {
        long setting = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT " + playerStatistic.getColumn() + " FROM users_statistics WHERE user_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                setting = resultSet.getInt(playerStatistic.getColumn());
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return setting;
    }

    public static String getStatisticString(int userId, PlayerStatistic playerStatistic) {
        String setting = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT " + playerStatistic.getColumn() + " FROM users_statistics WHERE user_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                setting = resultSet.getString(playerStatistic.getColumn());
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return setting;
    }

    public static Map<PlayerStatistic, String> getStatistics(int userId) {
        Map<PlayerStatistic, String> values = new HashMap<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users_statistics WHERE user_id = ?", sqlConnection);
            preparedStatement.setLong(1, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                for (PlayerStatistic playerStatistic : PlayerStatistic.values()) {
                    if (playerStatistic.isDateTime()) {
                        if (resultSet.getTime(playerStatistic.getColumn()) != null) {
                            values.put(playerStatistic, String.valueOf(resultSet.getTime(playerStatistic.getColumn()).getTime() / 1000L));
                        } else {
                            values.put(playerStatistic, null);
                        }
                    } else {
                        values.put(playerStatistic, resultSet.getString(playerStatistic.getColumn()));
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

        return values;
    }

    public static void newStatistics(int userId, String activationCode) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO users_statistics (user_id, activation_code) VALUES (?, ?)", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, activationCode);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}
