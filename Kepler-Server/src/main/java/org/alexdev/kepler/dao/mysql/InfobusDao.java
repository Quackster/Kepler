package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.Kepler;
import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.infobus.InfobusPoll;
import org.alexdev.kepler.game.infobus.InfobusPollData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfobusDao {
    public static List<InfobusPoll> getInfobusPolls() {
        List<InfobusPoll> polls = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM infobus_polls ORDER BY created_at DESC", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                polls.add(fill(resultSet));
            }
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return polls;
    }

    public static int createInfobusPoll(int initiatedBy, InfobusPollData pollData) {
        int pollId = -1;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO infobus_polls (initiated_by, poll_data) VALUES (?, ?)", sqlConnection);
            preparedStatement.setInt(1, initiatedBy);
            preparedStatement.setString(2, Kepler.getGson().toJson(pollData));
            preparedStatement.execute();
            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                pollId = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }


        return pollId;
    }

    public static void delete(int id) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM infobus_polls WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static InfobusPoll get(int id) {
        InfobusPoll infobusPoll = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM infobus_polls WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                infobusPoll = fill(resultSet);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return infobusPoll;
    }

    public static void saveInfobusPoll(int id, InfobusPollData pollData) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE infobus_polls SET poll_data = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, Kepler.getGson().toJson(pollData));
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void addAnswer(int pollId, int answer, int userId) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO infobus_polls_answers (user_id, poll_id, answer) VALUES (?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, pollId);
            preparedStatement.setInt(3, answer);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static boolean hasAnswer(int pollId, int userId) {
        boolean exists = false;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM infobus_polls_answers WHERE user_id = ? AND poll_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, pollId);
            resultSet = preparedStatement.executeQuery();
            exists = resultSet.next();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return exists;
    }

    public static Map<Integer, Integer> getAnswers(int pollId) {
        Map<Integer, Integer> answers = new HashMap<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT COUNT(*) AS votes, answer FROM infobus_polls_answers WHERE poll_id = ? GROUP BY answer", sqlConnection);
            preparedStatement.setInt(1, pollId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                answers.put(resultSet.getInt("answer"), resultSet.getInt("votes"));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return answers;
    }

    public static void clearAnswers(int pollId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM infobus_polls_answers WHERE poll_id = ?", sqlConnection);
            preparedStatement.setInt(1, pollId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    private static InfobusPoll fill(ResultSet resultSet) throws SQLException {
        return new InfobusPoll(resultSet.getInt("id"), resultSet.getInt("initiated_by"),
                Kepler.getGson().fromJson(resultSet.getString("poll_data"), InfobusPollData.class),
                resultSet.getTime("created_at").getTime() / 1000L);
    }

}
