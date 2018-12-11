package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.moderation.ModerationActionType;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ModerationDao {
   public static void addLog(ModerationActionType type, int userId, int targetId, String message, String extraNotes) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO housekeeping_audit_log (action, user_id, target_id, message, extra_notes) VALUES (?, ?, ?, ?, ?)", sqlConnection);
            preparedStatement.setString(1, type.name().toLowerCase());
            preparedStatement.setInt(2, targetId);
            preparedStatement.setInt(3, userId);
            preparedStatement.setString(4, message);
            preparedStatement.setString(5, extraNotes);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}
