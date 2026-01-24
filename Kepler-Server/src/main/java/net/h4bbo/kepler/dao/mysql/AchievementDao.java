package net.h4bbo.kepler.dao.mysql;

import net.h4bbo.kepler.dao.Storage;
import net.h4bbo.kepler.game.achievements.AchievementInfo;
import net.h4bbo.kepler.game.achievements.user.UserAchievement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AchievementDao {
    public static Map<Integer, AchievementInfo> getAchievements() {
        Map<Integer, AchievementInfo> achievementsList = new HashMap<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM achievements WHERE disabled = 0", connection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                var info = new AchievementInfo(
                        resultSet.getInt("id"), resultSet.getString("achievement"),
                        resultSet.getInt("level"), resultSet.getInt("reward_pixels"),
                        resultSet.getInt("progress_needed"));

                achievementsList.put(info.getId(), info);
            }
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(connection);
        }

        return achievementsList;
    }

    public static List<UserAchievement> getUserAchievements(int id) {
        List<UserAchievement> achievementsList = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users_achievements WHERE user_id = ?", connection);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                achievementsList.add(new UserAchievement(
                        resultSet.getInt("achievement_id"), resultSet.getInt("user_id"), resultSet.getInt("progress")));
            }
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(connection);
        }

        return achievementsList;

    }

    public static void newUserAchievement(int userId, UserAchievement userAchievement) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO users_achievements (achievement_id, user_id, progress) VALUES (?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, userAchievement.getAchievementInfo().getId());
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, userAchievement.getProgress());
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void saveUserAchievement(int userId, UserAchievement userAchievement) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users_achievements SET progress = ? WHERE user_id = ? AND achievement_id = ?", sqlConnection);
            preparedStatement.setInt(1, userAchievement.getProgress());
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, userAchievement.getAchievementInfo().getId());
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}
