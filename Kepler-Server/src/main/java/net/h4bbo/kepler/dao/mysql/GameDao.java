package net.h4bbo.kepler.dao.mysql;

import net.h4bbo.kepler.Kepler;
import net.h4bbo.kepler.dao.Storage;
import net.h4bbo.kepler.game.games.GameSpawn;
import net.h4bbo.kepler.game.games.enums.GameType;
import net.h4bbo.kepler.game.games.battleball.BattleBallMap;
import net.h4bbo.kepler.game.games.history.GameHistory;
import net.h4bbo.kepler.game.games.history.GameHistoryData;
import net.h4bbo.kepler.game.games.player.GameRank;
import net.h4bbo.kepler.game.room.models.RoomModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

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
                ranks.add(new GameRank(resultSet.getString("type"),
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
                String modelName = "bb" + "_arena_" + resultSet.getInt("map_id");

                if (!resultSet.getString("game_type").equals("battleball")) {
                    modelName = "ss_arena_" + resultSet.getInt("map_id");
                }

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
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM games_maps WHERE game_type = 'battleball'", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                maps.add(new BattleBallMap(resultSet.getInt("map_id"), GameType.BATTLEBALL, resultSet.getString("tile_map")));
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
                        resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("rotation")));
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

    /*public static List<GameHistory> getTopTeams() {
        List<GameHistory> teams = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM games_played_history ORDER BY team_points DESC LIMIT 3", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String json = resultSet.getString("player_scores");

                try {
                    GameHistoryData gamePlayedHistory = Havana.getGson().fromJson(json, GameHistoryData.class);
                    teams.add(new GameHistory(resultSet.getInt("team_type"), gamePlayedHistory));
                } catch (Exception ex) {

                }
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }


        return teams;
    }

    public static HashMap<String, Integer> getTopPlayers() {
        LinkedHashMap<String, Integer> players = new LinkedHashMap<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT *,(battleball_points + snowstorm_points) AS game_points FROM users INNER JOIN users_statistics ON users_statistics.user_id = users.id ORDER BY (battleball_points + snowstorm_points) DESC LIMIT 5", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                players.put(resultSet.getString("username"), resultSet.getInt("game_points"));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return players;
    }*/


    public static void resetMonthlyXp() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users_statistics SET battleball_score_month = 0, snowstorm_score_month = 0, wobble_squabble_score_month = 0, xp_earned_month = 0 " +
                    "WHERE (battleball_score_month > 0) OR (snowstorm_score_month > 0) OR (wobble_squabble_score_month > 0) OR (xp_earned_month > 0)", sqlConnection);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void saveTeamHistory(String uniqueId, String gameName, int mapCreator, int mapId, int winningTeam, int winningTeamScore, String extraData, GameType gameType, String gameHistoryData) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO games_played_history (id, game_name, game_creator, game_type, map_id, winning_team, winning_team_score, extra_data, team_data) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", sqlConnection);
            preparedStatement.setString(1, uniqueId);
            preparedStatement.setString(2, gameName);
            preparedStatement.setInt(3, mapCreator);
            preparedStatement.setString(4, gameType.name());
            preparedStatement.setInt(5, mapId);
            preparedStatement.setInt(6, winningTeam);
            preparedStatement.setInt(7, winningTeamScore);
            preparedStatement.setString(8, extraData);
            preparedStatement.setString(9, gameHistoryData);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static List<GameHistory> getLastPlayedGames(GameType gameType) {
        List<GameHistory> games = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT games_played_history.*, users.username AS game_creator_name FROM games_played_history INNER JOIN users ON users.id = games_played_history.game_creator WHERE game_type = ? ORDER BY played_at DESC LIMIT 15", sqlConnection);
            preparedStatement.setString(1, gameType.name());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                var gameHistory = new GameHistory(Kepler.getGson().fromJson(resultSet.getString("team_data"), GameHistoryData.class));

                gameHistory.setName(resultSet.getString("game_name"));
                gameHistory.setGameCreator(resultSet.getString("game_creator_name"));
                gameHistory.setMapId(resultSet.getInt("map_id"));
                gameHistory.setGameType(GameType.valueOf(resultSet.getString("game_type")));
                gameHistory.setWinningTeam(resultSet.getInt("winning_team"));
                gameHistory.setWinningTeamScore(resultSet.getInt("winning_team_score"));
                gameHistory.setExtraData(resultSet.getString("extra_data"));

                games.add(gameHistory);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return games;
    }
}
