package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.recycler.RecyclerReward;

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
                recyclerRewardList.add(new RecyclerReward(resultSet.getInt("id"), resultSet.getString("sale_code"), resultSet.getInt("item_cost")));
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
}
