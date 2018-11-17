package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.games.enums.GameType;
import org.alexdev.kepler.game.games.battleball.BattleBallMap;
import org.alexdev.kepler.game.games.player.GameRank;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.room.models.RoomModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameDao {
    public static List<GameRank> getRanks() {
        List<GameRank> ranks = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM games_ranks", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ranks.add(new GameRank(resultSet.getInt("id"), resultSet.getString("type"),
                        resultSet.getString("title"), resultSet.getInt("min_points"),
                        resultSet.getInt("max_points")));
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

    public static List<RoomModel> getGameMaps() {
        List<RoomModel> maps = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM games_maps", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String modelName = (resultSet.getString("type").equalsIgnoreCase("battleball") ? "bb" : "ss") + "_arena_" + resultSet.getInt("id");
                maps.add(new RoomModel(modelName, modelName, Integer.MAX_VALUE, Integer.MAX_VALUE, Double.MAX_VALUE, 0, resultSet.getString("heightmap"), null));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }


        return maps;
    }

    public static List<BattleBallMap> getBattleballTileMaps() {
        List<BattleBallMap> maps = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM games_maps WHERE type = 'battleball'", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                maps.add(new BattleBallMap(resultSet.getInt("id"), GameType.BATTLEBALL, resultSet.getString("tile_map")));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }


        return maps;
    }

    public static List<GameSpawn> getGameSpawns() {
        List<GameSpawn> spawns = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM games_player_spawns", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                spawns.add(new GameSpawn(resultSet.getInt("team_id"),  resultSet.getInt("map_id"), resultSet.getString("type"),
                        resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z")));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }


        return spawns;
    }

    /**
     * Atomically increase tickets.
     *
     * @param details the player details
     */
    public static void increasePoints(PlayerDetails details, GameType type, int amount) {
        String column = type.name().toLowerCase() + "_points";

        Connection conn = null;
        PreparedStatement updateQuery = null;
        PreparedStatement fetchQuery = null;
        ResultSet row = null;

        try {
            conn = Storage.getStorage().getConnection();

            // We disable autocommit to make sure the following queries share the same atomic transaction
            conn.setAutoCommit(false);

            // Increase credits
            updateQuery = Storage.getStorage().prepare("UPDATE users SET " + column + " = " + column + " + ? WHERE id = ?", conn);
            updateQuery.setInt(1, amount);
            updateQuery.setInt(2, details.getId());
            updateQuery.execute();

            // Fetch increased amount
            fetchQuery = Storage.getStorage().prepare("SELECT " + column + " FROM users WHERE id = ?", conn);
            fetchQuery.setInt(1, details.getId());
            row = fetchQuery.executeQuery();

            // Commit these queries
            conn.commit();

            // Set amount
            if (row != null && row.next()) {
                int updatedAmount = row.getInt(column);

                if (type == GameType.BATTLEBALL) {
                    details.setBattleballPoints(updatedAmount);
                }

                if (type == GameType.SNOWSTORM) {
                    details.setSnowStormPoints(updatedAmount);
                }
            }

        } catch (Exception e) {
            try {
                // Rollback these queries
                conn.rollback();
            } catch(SQLException re) {
                Storage.logError(re);
            }

            Storage.logError(e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ce) {
                Storage.logError(ce);
            }

            Storage.closeSilently(row);
            Storage.closeSilently(updateQuery);
            Storage.closeSilently(fetchQuery);
            Storage.closeSilently(conn);
        }
    }
}
