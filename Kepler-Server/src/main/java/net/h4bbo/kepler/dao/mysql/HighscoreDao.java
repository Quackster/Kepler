package net.h4bbo.kepler.dao.mysql;

import net.h4bbo.kepler.dao.Storage;
import net.h4bbo.kepler.game.games.enums.GameType;
import net.h4bbo.kepler.game.games.history.ScoreEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HighscoreDao {
    public static List<ScoreEntry> getScores(int limit, GameType gameType, int page, boolean viewMontly) {
        List<ScoreEntry> scoreEntryList = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT users.username AS username,users_statistics.* FROM users_statistics INNER JOIN users ON users.id = users_statistics.user_id ");

            if (viewMontly) {
                if (gameType == GameType.BATTLEBALL) {
                    query.append("WHERE battleball_score_month > 0 ");
                }

                if (gameType == GameType.SNOWSTORM) {
                    query.append("WHERE snowstorm_score_month > 0 ");
                }

                if (gameType == GameType.WOBBLE_SQUABBLE) {
                    query.append("WHERE wobble_squabble_score_month > 0 ");
                }
            } else {
                if (gameType == GameType.BATTLEBALL) {
                    query.append("WHERE battleball_score_all_time > 0 ");
                }

                if (gameType == GameType.SNOWSTORM) {
                    query.append("WHERE snowstorm_score_all_time > 0 ");
                }

                if (gameType == GameType.WOBBLE_SQUABBLE) {
                    query.append("WHERE wobble_squabble_score_all_time > 0 ");
                }
            }

            query.append("AND ((SELECT COUNT(*) FROM users_bans WHERE banned_value = users.id AND ban_type = 'USER_ID' AND NOW() > banned_until AND is_active = 1) = 0) ");
            query.append("ORDER BY ");

            if (viewMontly) {
                if (gameType == GameType.BATTLEBALL) {
                    query.append("battleball_score_month DESC ");
                }

                if (gameType == GameType.SNOWSTORM) {
                    query.append("snowstorm_score_month DESC ");
                }

                if (gameType == GameType.WOBBLE_SQUABBLE) {
                    query.append("wobble_squabble_score_month DESC ");
                }
            } else {
                if (gameType == GameType.BATTLEBALL) {
                    query.append("battleball_score_all_time DESC ");
                }

                if (gameType == GameType.SNOWSTORM) {
                    query.append("snowstorm_score_all_time DESC ");
                }

                if (gameType == GameType.WOBBLE_SQUABBLE) {
                    query.append("wobble_squabble_score_all_time DESC ");
                }
            }

            query.append( "LIMIT " + ((page - 1) * limit) + "," + limit);

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare(query.toString(), sqlConnection);
            resultSet = preparedStatement.executeQuery();

            int position = ((page - 1) * limit) + 1;
            
            while (resultSet.next()) {
                if (viewMontly) {
                    if (gameType == GameType.BATTLEBALL) {
                        scoreEntryList.add(new ScoreEntry(resultSet.getString("username"), resultSet.getLong("battleball_score_month"), position));
                    } else if (gameType == GameType.SNOWSTORM) {
                        scoreEntryList.add(new ScoreEntry(resultSet.getString("username"), resultSet.getLong("snowstorm_score_month"), position));
                    } else if (gameType == GameType.WOBBLE_SQUABBLE) {
                        scoreEntryList.add(new ScoreEntry(resultSet.getString("username"), resultSet.getLong("wobble_squabble_score_month"), position));
                    }
                } else {
                    if (gameType == GameType.BATTLEBALL) {
                        scoreEntryList.add(new ScoreEntry(resultSet.getString("username"), resultSet.getLong("battleball_score_all_time"), position));
                    } else if (gameType == GameType.SNOWSTORM) {
                        scoreEntryList.add(new ScoreEntry(resultSet.getString("username"), resultSet.getLong("snowstorm_score_all_time"), position));
                    } else if (gameType == GameType.WOBBLE_SQUABBLE) {
                        scoreEntryList.add(new ScoreEntry(resultSet.getString("username"), resultSet.getLong("wobble_squabble_score_all_time"), position));
                    }
                }
                position++;
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return scoreEntryList;
    }
}
