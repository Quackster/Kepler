package net.h4bbo.http.kepler.dao;

import net.h4bbo.kepler.dao.Storage;
import net.h4bbo.http.kepler.game.groups.DiscussionReply;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class ReplyDao {
    public static void read(int userId, List<DiscussionReply> replies) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT IGNORE INTO `cms_forums_read_replies` (user_id, reply_id) VALUES (?, ?)", sqlConnection);
            sqlConnection.setAutoCommit(false);

            for (DiscussionReply reply : replies) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, reply.getId());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            sqlConnection.setAutoCommit(true);

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static boolean hasRead(int userId, int replyId) {
        boolean hasRated = false;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM `cms_forums_read_replies` WHERE user_id = ? AND reply_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, replyId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                hasRated = true;
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return hasRated;
    }
}
