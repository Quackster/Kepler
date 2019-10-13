package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.recycler.RecyclerReward;
import org.alexdev.kepler.game.recycler.RecyclerSession;
import org.alexdev.kepler.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RecyclerDao {
    public static List<RecyclerReward> getRewards() {
        List<RecyclerReward> recyclerRewardList = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM recycler_rewards ORDER BY id ASC", sqlConnection);
            resultSet =  preparedStatement.executeQuery();

            while (resultSet.next()) {
                recyclerRewardList.add(new RecyclerReward(resultSet.getInt("id"), resultSet.getString("sale_code"), resultSet.getInt("item_cost"),
                        resultSet.getInt("recycling_session_time_seconds"), resultSet.getInt("collection_time_seconds")));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return recyclerRewardList;
    }

    public static RecyclerSession getSession(int userId) {
        RecyclerSession recyclerSession = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM recycler_sessions WHERE user_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet =  preparedStatement.executeQuery();

            if (resultSet.next()) {
                recyclerSession = new RecyclerSession(resultSet.getInt("reward_id"), resultSet.getTime("session_started").getTime() / 1000L, resultSet.getString("items"));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return recyclerSession;
    }

    public static RecyclerSession createSession(int userId, int rewardId, String items) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO recycler_sessions (user_id, reward_id, items) VALUES (?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, rewardId);
            preparedStatement.setString(3, items);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return new RecyclerSession(rewardId, DateUtil.getCurrentTimeSeconds(), items);
    }


    public static void deleteSession(int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM recycler_sessions WHERE user_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}
