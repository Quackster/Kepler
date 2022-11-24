package org.alexdev.kepler.dao.mysql;


import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.reward.Reward;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RewardDao {

    public static List<Reward> getAvailableRewards(int userId) {
        List<Reward> rewards = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT rewards.id, rewards.salecodes, rewards.available_from, rewards.available_to, rewards.description FROM `rewards` LEFT JOIN rewards_redeemed on rewards.id = rewards_redeemed.id WHERE rewards.id NOT IN (SELECT user_id FROM rewards_redeemed where user_id = ?) AND CURRENT_TIMESTAMP() between available_from and available_to", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Reward reward = new Reward(
                        resultSet.getInt("id"),
                        resultSet.getString("description"),
                        resultSet.getDate("available_from"),
                        resultSet.getDate("available_to"),
                        resultSet.getString("salecodes")
                );
                rewards.add(reward);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return rewards;
    }

    public static void redeemReward(int id, int user_id) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO rewards_redeemed (user_id, reward_id) VALUES (?, ?)", sqlConnection);
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, user_id);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}
