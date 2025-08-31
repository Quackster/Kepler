package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.item.Transaction;
import org.alexdev.kepler.game.moderation.ModerationActionType;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransactionDao {
    public static void createTransaction(int userId, String itemId, String catalogueId, int amount, String description, int creditCost, int pixelCost, boolean visible) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO users_transactions (user_id, item_id, catalogue_id, amount, description, credit_cost, pixel_cost, is_visible) VALUES (?, ?, ?, ? ,?, ?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, itemId);
            preparedStatement.setString(3, catalogueId);
            preparedStatement.setInt(4, amount);
            preparedStatement.setString(5, description);
            preparedStatement.setInt(6, creditCost);
            preparedStatement.setInt(7, pixelCost);
            preparedStatement.setInt(8, visible ? 1 : 0);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static List<Transaction> getTransactionByItem(int itemId) {
        List<Transaction> transactions = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users_transactions WHERE item_id = ? ORDER BY users_transactions.created_at DESC", sqlConnection);
            preparedStatement.setInt(1, itemId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                transactions.add(new Transaction(resultSet.getString("item_id").split(","), resultSet.getString("description"), resultSet.getInt("credit_cost"), resultSet.getInt("pixel_cost"), resultSet.getInt("amount"),
                        resultSet.getTime("created_at").getTime() / 1000L));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return transactions;
    }

    public static List<Transaction> getTransactionsPastMonth(String searchQuery, boolean viewAll) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(System.currentTimeMillis());
        int month = currentCalendar.get(Calendar.MONTH) + 1;
        int year = currentCalendar.get(Calendar.YEAR);

        List<Transaction> transactions = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users_transactions INNER JOIN users ON users.id = users_transactions.user_id WHERE MONTH(users_transactions.created_at) = ? AND YEAR(users_transactions.created_at) = ? AND user_id = ? OR username = ? ORDER BY users_transactions.created_at DESC", sqlConnection);
            preparedStatement.setInt(1, month);
            preparedStatement.setInt(2, year);
            preparedStatement.setInt(3, StringUtils.isNumeric(searchQuery) ? Integer.parseInt(searchQuery) : -1);
            preparedStatement.setString(4, searchQuery);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                boolean isVisible = resultSet.getBoolean("is_visible");

                if (!isVisible && !viewAll) {
                    continue;
                }

                transactions.add(new Transaction(resultSet.getString("item_id").split(","), resultSet.getString("description"), resultSet.getInt("credit_cost"), resultSet.getInt("pixel_cost"), resultSet.getInt("amount"),
                        resultSet.getTime("created_at").getTime() / 1000L));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return transactions;
    }

    public static List<Transaction> getTransactions(int userId, int month, int year, boolean viewAll) {
        List<Transaction> transactions = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users_transactions WHERE YEAR(created_at) = ? AND MONTH(created_at) = ? AND user_id = ? ORDER BY created_at DESC", sqlConnection);
            preparedStatement.setInt(1, year);
            preparedStatement.setInt(2, month);
            preparedStatement.setInt(3, userId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                boolean isVisible = resultSet.getBoolean("is_visible");

                if (!isVisible && !viewAll) {
                    continue;
                }

                transactions.add(new Transaction(resultSet.getString("item_id").split(","), resultSet.getString("description"), resultSet.getInt("credit_cost"), resultSet.getInt("pixel_cost"), resultSet.getInt("amount"),
                        resultSet.getTime("created_at").getTime() / 1000L));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return transactions;
    }
}
