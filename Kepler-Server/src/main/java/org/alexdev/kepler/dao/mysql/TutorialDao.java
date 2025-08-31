package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TutorialDao {
   public static void updateTutorialMode(int userId, boolean tutorialFinished) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET tutorial_finished = ? WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, tutorialFinished ? 1 : 0);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}
