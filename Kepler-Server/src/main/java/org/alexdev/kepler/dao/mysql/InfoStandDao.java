package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.infostand.InfoStand;
import org.alexdev.kepler.game.infostand.InfoStandActive;
import org.alexdev.kepler.game.infostand.InfoStandEntry;
import org.alexdev.kepler.game.infostand.InfoStandProp;
import org.alexdev.kepler.game.infostand.InfoStandShopEntry;
import org.alexdev.kepler.game.item.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InfoStandDao {

    public static List<InfoStandShopEntry> getShop() {
        List<InfoStandShopEntry> entries = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM info_stand_shop", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                InfoStandShopEntry cataloguePackage = new InfoStandShopEntry(
                        resultSet.getInt("id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("product_code"),
                        InfoStandProp.fromId(resultSet.getInt("type")),
                        resultSet.getInt("currency"),
                        resultSet.getInt("price"));

                entries.add(cataloguePackage);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return entries;
    }

    public static List<InfoStandEntry> getUserOwned(int userId) {
        List<InfoStandEntry> result = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM info_stand WHERE user_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                result.add(new InfoStandEntry(
                        resultSet.getString("product_id"),
                        InfoStandProp.fromId(resultSet.getInt("product_type"))));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return result;
    }

    public static void addUserProp(InfoStand infoStand, String productId, InfoStandProp type) {
        if (infoStand.ownsProp(type, productId)) {
            return;
        }

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO info_stand (user_id, product_id, product_type) VALUES (?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, infoStand.getPlayer().getDetails().getId());
            preparedStatement.setString(2, productId);
            preparedStatement.setInt(3, type.getId());
            preparedStatement.execute();

            infoStand.addProp(type, productId);
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static InfoStandActive getUserActive(int userId) {
        InfoStandActive result = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users_info_stand WHERE user_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                result = new InfoStandActive();
                result.fill(resultSet.getInt("plate"),
                        resultSet.getInt("furni"),
                        resultSet.getString("expression"),
                        resultSet.getString("action"),
                        resultSet.getInt("direction"));
            } else {
                result = new InfoStandActive();
            }
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return result;
    }

    public static void setUserActive(InfoStand infoStand, int plate, int furni, String expression, String action, int direction) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("REPLACE INTO users_info_stand (user_id, plate, furni, expression, action, direction) VALUES (?, ?, ?, ?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, infoStand.getPlayer().getDetails().getId());
            preparedStatement.setInt(2, plate);
            preparedStatement.setInt(3, furni);
            preparedStatement.setString(4, expression);
            preparedStatement.setString(5, action);
            preparedStatement.setInt(6, direction);
            preparedStatement.execute();

            infoStand.getActive().fill(plate, furni, expression, action, direction);
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}
