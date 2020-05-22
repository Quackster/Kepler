package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.public_items.PublicItemData;
import org.alexdev.kepler.game.item.base.ItemDefinition;
import org.alexdev.kepler.game.room.RoomData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ItemDao {
    
    /**
     * Get the item definitions.
     *
     * @return the list of item definitions
     */
    public static Map<Integer, ItemDefinition> getItemDefinitions() {
        Map<Integer, ItemDefinition> definitions = new HashMap<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM items_definitions", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ItemDefinition definition = new ItemDefinition(resultSet.getInt("id"), resultSet.getString("sprite"),
                        resultSet.getString("name"), resultSet.getString("description"),
                        resultSet.getString("behaviour"), resultSet.getString("interactor"), resultSet.getDouble("top_height"),
                        resultSet.getInt("length"), resultSet.getInt("width"), resultSet.getString("colour"), resultSet.getString("drink_ids"),
                        resultSet.getBoolean("is_recyclable"));

                definitions.put(definition.getId(), definition);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return definitions;
    }

    /**
     * Get the item definitions.
     *
     * @return the list of item definitions
     */
    public static List<PublicItemData> getPublicItemData(String roomModel) {
       List<PublicItemData> itemDataList = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM public_items WHERE room_model = ?", sqlConnection);
            preparedStatement.setString(1, roomModel);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                PublicItemData itemData = new PublicItemData(resultSet.getString("id"), resultSet.getString("room_model"),
                        resultSet.getString("sprite"), resultSet.getInt("x"), resultSet.getInt("y"),
                        resultSet.getDouble("z"), resultSet.getInt("rotation"),  resultSet.getDouble("top_height"),
                        resultSet.getInt("length"), resultSet.getInt("width"), resultSet.getString("behaviour"),
                        resultSet.getString("current_program"));

                itemDataList.add(itemData);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return itemDataList;
    }

    /**
     * Create new item entry with the definition id, user id and custom data. It will
     * override the current item id with its database id.
     *
     * @param item the item to create
     */
    public static void newItem(Item item) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet row = null;

        int itemId = 0;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO items (user_id, definition_id, custom_data) VALUES (?,?,?)", sqlConnection);
            preparedStatement.setInt(1, item.getOwnerId());
            preparedStatement.setInt(2, item.getDefinition().getId());
            preparedStatement.setString(3, item.getCustomData());
            preparedStatement.executeUpdate();

            row = preparedStatement.getGeneratedKeys();

            if (row != null && row.next()) {
                itemId = row.getInt(1);
            }

        } catch (SQLException e) {
            Storage.logError(e);
            throw e;
        } finally {
            Storage.closeSilently(row);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        item.setId(itemId);
    }

    /**
     * Get the inventory list of items.
     *
     * @param userId the id of the user to get the inventory for
     * @return the list of items
     */
    public static List<Item> getInventory(int userId) {
        List<Item> items = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM items WHERE user_id = ? AND room_id = 0 ORDER BY order_id ASC", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Item item = new Item();
                fill(item, resultSet);
                items.add(item);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return items;
    }

    /**
     * Get the item by item id
     *
     * @param itemId the id of the item to to get
     * @return the item
     */
    public static Item getItem(int itemId) {
        Item item = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM items WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, itemId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                item = new Item();
                fill(item, resultSet);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return item;
    }

    /**
     * Get the room list of items.
     *
     * @param roomData the room data instance
     * @return the list of items
     */
    public static List<Item> getRoomItems(RoomData roomData) {
        List<Item> items = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM items WHERE room_id = ? ORDER BY z", sqlConnection);
            preparedStatement.setInt(1, roomData.getId());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Item item = new Item();
                fill(item, resultSet);
                items.add(item);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return items;
    }

    /**
     * Delete an entire list of items at once.
     *
     * @param items the list of items
     */
    public static void deleteItems(List<Integer> items) {
        if (items.size() > 0) {
            Connection sqlConnection = null;
            PreparedStatement preparedStatement = null;

            try {
                sqlConnection = Storage.getStorage().getConnection();
                preparedStatement = Storage.getStorage().prepare("DELETE FROM items WHERE id = ?", sqlConnection);
                sqlConnection.setAutoCommit(false);

                for (Integer itemId : items) {
                    preparedStatement.setInt(1, itemId);
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
    }

    /**
     * Redeem credit furniture atomicly
     *
     * @param amount credit amount to increase by
     * @param userID user ID
     */
    public static int redeemCreditItem(int amount, int itemID, int userID) {
        int updatedAmount = -1;
        Connection conn = null;
        PreparedStatement deleteQuery = null;
        PreparedStatement updateQuery = null;
        PreparedStatement fetchQuery = null;
        ResultSet row = null;

        try {
            conn = Storage.getStorage().getConnection();

            // We disable autocommit to make sure the following queries share the same atomic transaction
            conn.setAutoCommit(false);

            deleteQuery = Storage.getStorage().prepare("DELETE FROM items WHERE id = ?", conn);
            deleteQuery.setInt(1, itemID);
            deleteQuery.execute();

            // Increase credits
            updateQuery = Storage.getStorage().prepare("UPDATE users SET credits = credits + ? WHERE id = ?", conn);
            updateQuery.setInt(1, amount);
            updateQuery.setInt(2, userID);
            updateQuery.execute();

            // Fetch increased amount
            fetchQuery = Storage.getStorage().prepare("SELECT credits FROM users WHERE id = ?", conn);
            fetchQuery.setInt(1, userID);
            row = fetchQuery.executeQuery();

            // Commit these queries
            conn.commit();

            // Set amount
            if (row != null && row.next()) {
                updatedAmount = row.getInt("credits");
            }

        } catch (Exception e) {
            // Reset amount
            updatedAmount = -1;

            try {
                // Rollback these queries
                conn.rollback();
            } catch(SQLException re) {
                Storage.logError(re);
            }

            Storage.logError(e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ce) {
                Storage.logError(ce);
            }

            Storage.closeSilently(row);
            Storage.closeSilently(deleteQuery);
            Storage.closeSilently(updateQuery);
            Storage.closeSilently(fetchQuery);
            Storage.closeSilently(conn);
        }

        return updatedAmount;
    }

    /**
     * Update item by item instance.
     *
     * @param item the instance of the item to update it
     */
    public static void updateItem(Item item) {
        updateItems(List.of(item));
    }

    /**
     * Update an entire list of items at once.
     *
     * @param items the list of items
     */
    public static void updateItems(Collection<Item> items) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE items SET user_id = ?, room_id = ?, definition_id = ?, x = ?, y = ?, z = ?, rotation = ?, wall_position = ?, custom_data = ?, order_id = ?, is_hidden = ? WHERE id = ?", sqlConnection);
            sqlConnection.setAutoCommit(false);

            for (Item item : items) {
                preparedStatement.setInt(1, item.getOwnerId());
                preparedStatement.setInt(2, item.getRoomId());
                preparedStatement.setInt(3, item.getDefinition().getId());
                preparedStatement.setInt(4, item.getPosition().getX());
                preparedStatement.setInt(5, item.getPosition().getY());
                preparedStatement.setDouble(6, item.getPosition().getZ());
                preparedStatement.setInt(7, item.getPosition().getRotation());
                preparedStatement.setString(8, item.getWallPosition());
                preparedStatement.setString(9, item.getCustomData());
                preparedStatement.setInt(10, item.getOrderId());
                preparedStatement.setInt(11, item.isHidden() ? 1 : 0);
                preparedStatement.setLong(12, item.getId());
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

    /**
     * Fill item with data retrieved from the SQL query.
     *
     * @param item the item to fill data for
     * @param resultSet the result set returned with the data
     * @throws SQLException an exception if an error happened
     */
    private static void fill(Item item, ResultSet resultSet) throws SQLException {
        item.fill(resultSet.getInt("id"), resultSet.getInt("order_id"), resultSet.getInt("user_id"), resultSet.getInt("room_id"),
                resultSet.getInt("definition_id"), resultSet.getInt("x"), resultSet.getInt("y"),
                resultSet.getDouble("z"), resultSet.getInt("rotation"), resultSet.getString("wall_position"),
                resultSet.getString("custom_data"), resultSet.getBoolean("is_hidden"));
    }
}
