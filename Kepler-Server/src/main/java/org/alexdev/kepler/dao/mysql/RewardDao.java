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
            preparedStatement = Storage.getStorage().prepare("SELECT \n" +
                    "\tt1.id, \n" +
                    "    t1.item_definitions,\n" +
                    "    t1.available_from, \n" +
                    "    t1.available_to, \n" +
                    "    t1.description, \n" +
                    "    t1.badge\n" +
                    "FROM `rewards` as t1\n" +
                    "WHERE NOT EXISTS \n" +
                    "    (SELECT t2.reward_id, t2.user_id \n" +
                    "     FROM rewards_redeemed as t2\n" +
                    "     WHERE t2.reward_id = t1.id and t2.user_id = ?) AND CURRENT_TIMESTAMP() between t1.available_from and t1.available_to;", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Reward reward = new Reward(
                        resultSet.getInt("id"),
                        resultSet.getString("description"),
                        resultSet.getDate("available_from"),
                        resultSet.getDate("available_to"),
                        resultSet.getString("item_definitions"),
                        resultSet.getString("badge")
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
            preparedStatement = Storage.getStorage().prepare("INSERT INTO rewards_redeemed (reward_id, user_id) VALUES (?, ?)", sqlConnection);
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
