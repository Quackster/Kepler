package net.h4bbo.http.kepler.dao;

import net.h4bbo.kepler.dao.Storage;
import net.h4bbo.http.kepler.game.minimail.MinimailMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MinimailDao {
    public static MinimailMessage getMessage(int messageId, int targetId) {
        MinimailMessage message = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_minimail WHERE id = ? AND (target_id = ? OR sender_id = ?)", sqlConnection);
            preparedStatement.setInt(1, messageId);
            preparedStatement.setInt(2, targetId);
            preparedStatement.setInt(3, targetId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                message = fill(resultSet);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return message;
    }

    public static List<MinimailMessage> getMessages(int userId) {
        List<MinimailMessage> messages = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_minimail WHERE to_id = ? AND is_trash = 0 AND is_deleted = 0 AND target_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                messages.add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return messages;
    }

    public static List<MinimailMessage> getMessagesSent(int userId) {
        List<MinimailMessage> messages = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_minimail WHERE sender_id = ? AND is_trash = 0 AND is_deleted = 0 AND target_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                messages.add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return messages;
    }

    public static void createMessages(List<MinimailMessage> minimailMessages) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO cms_minimail (target_id, sender_id, to_id, subject, message, conversation_id) VALUES (?, ?, ?, ?, ?, ?)", sqlConnection);
            sqlConnection.setAutoCommit(false);

            for (MinimailMessage minimailMessage : minimailMessages) {
                preparedStatement.setInt(1, minimailMessage.getTargetId());
                preparedStatement.setInt(2, minimailMessage.getSenderId());
                preparedStatement.setInt(3,  minimailMessage.getToId());
                preparedStatement.setString(4, minimailMessage.getSubject());
                preparedStatement.setString(5, minimailMessage.getMessage());
                preparedStatement.setInt(6, minimailMessage.getConversationId());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            sqlConnection.setAutoCommit(true);

        } catch (Exception ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void updateMessage(MinimailMessage minimailMessage) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE cms_minimail SET is_trash = ?, conversation_id = ?, is_read = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, minimailMessage.isTrash() ? 1 : 0);
            preparedStatement.setInt(2, minimailMessage.getConversationId());
            preparedStatement.setInt(3, minimailMessage.isRead() ? 1 : 0);
            preparedStatement.setInt(4, minimailMessage.getId());
            preparedStatement.execute();
        } catch (Exception ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void deleteMessage(MinimailMessage minimailMessage) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE cms_minimail SET is_deleted = 1 WHERE id = ? AND target_id = ?", sqlConnection);
            preparedStatement.setInt(1, minimailMessage.getId());
            preparedStatement.setInt(2, minimailMessage.getTargetId());
            preparedStatement.execute();
        } catch (Exception ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }


    public static void emptyTrash(int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE cms_minimail SET is_deleted = 1 WHERE is_trash = 1 AND target_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();
        } catch (Exception ex) {
            Storage.logError(ex);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static List<MinimailMessage> getMessagesConversation(int userId, int conversationId) {
        List<MinimailMessage> messages = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_minimail WHERE conversation_id = ? AND target_id = ? OR (sender_id = " + userId + " AND id = " + conversationId + ")", sqlConnection);
            preparedStatement.setInt(1, conversationId);
            preparedStatement.setInt(2, userId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                messages.add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return messages;
    }

    public static List<MinimailMessage> getMessageTrash(int userId) {
        List<MinimailMessage> messages = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_minimail WHERE is_trash = 1 AND is_deleted = 0 AND target_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                messages.add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return messages;
    }

    private static MinimailMessage fill(ResultSet resultSet) throws SQLException {
        return new MinimailMessage(resultSet.getInt("id"), resultSet.getInt("target_id"), resultSet.getInt("to_id"), resultSet.getInt("sender_id"),
                resultSet.getBoolean("is_read"), resultSet.getString("subject"), resultSet.getString("message"), resultSet.getTime("date_sent").getTime() / 1000L,
                resultSet.getInt("conversation_id"), resultSet.getBoolean("is_trash"));
    }
}
