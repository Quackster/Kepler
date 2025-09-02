package net.h4bbo.http.kepler.dao;

import net.h4bbo.kepler.dao.Storage;
import net.h4bbo.http.kepler.game.homes.Widget;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WidgetDao {
    public static List<Widget> getHomeWidgets(int userId) {
        List<Widget> widgets = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_stickers WHERE user_id = ? AND group_id = 0", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                widgets.add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return widgets;
    }

    public static List<Widget> getHomeWidgets(int userId, boolean isPlaced) {
        List<Widget> widgets = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_stickers WHERE user_id = ? AND group_id = 0 AND is_placed = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, isPlaced ? 1 : 0);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                widgets.add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return widgets;
    }

    public static List<Widget> getGroupWidgets(int groupId) {
        List<Widget> widgets = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_stickers WHERE group_id = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                widgets.add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return widgets;
    }

    public static List<Widget> getGroupWidgets(int groupId, boolean isPlaced) {
        List<Widget> widgets = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_stickers WHERE group_id = ? AND is_placed = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, isPlaced ? 1 : 0);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                widgets.add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return widgets;
    }

    public static List<Widget> getInventoryWidgets(int userId, int typeId) {
        List<Widget> widgets = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_stickers INNER JOIN cms_stickers_catalogue ON cms_stickers_catalogue.id = cms_stickers.sticker_id WHERE cms_stickers.user_id = ? AND cms_stickers_catalogue.type = ? AND cms_stickers.group_id = 0 AND is_placed = 0", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, typeId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                widgets.add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return widgets;
    }

    public static List<Widget> getInventoryWidgets(int userId) {
        List<Widget> widgets = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_stickers WHERE user_id = ? AND group_id = 0 AND is_placed = 0", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                widgets.add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return widgets;
    }

    public static Widget purchaseWidget(int userId, int x, int y, int z, int skinId, int stickerId, String text, int groupId, boolean isPlaced) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        int widgetId = 0;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO cms_stickers (user_id, x, y, z, skin_id, sticker_id, text, group_id, is_placed) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, x);
            preparedStatement.setInt(3, y);
            preparedStatement.setInt(4, z);
            preparedStatement.setInt(5, skinId);
            preparedStatement.setInt(6, stickerId);
            preparedStatement.setString(7, text);
            preparedStatement.setInt(8, groupId);
            preparedStatement.setInt(9, isPlaced ? 1 : 0);
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                widgetId = resultSet.getInt(1);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return new Widget(widgetId, userId, x, y, z, stickerId, skinId, groupId, text, 1, isPlaced, null);
    }

    public static void save(Widget widget) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE cms_stickers SET group_id = ?, x = ?, y = ?, z = ?, skin_id = ?, text = ?, is_placed = ?, extra_data = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, widget.getGroupId());
            preparedStatement.setInt(2, widget.getX());
            preparedStatement.setInt(3, widget.getY());
            preparedStatement.setInt(4, widget.getZ());
            preparedStatement.setInt(5, widget.getSkinId());
            preparedStatement.setString(6, widget.getText());
            preparedStatement.setInt(7, widget.isPlaced() ? 1 : 0);
            preparedStatement.setString(8, widget.getExtraData());
            preparedStatement.setInt(9, widget.getId());
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void delete(int widgetId, int groupId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM cms_stickers WHERE group_id = ? AND id = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, widgetId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }


    public static void deleteHomeNote(int stickieId, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM cms_stickers WHERE group_id = 0 AND is_placed = 1 AND user_id = ? AND id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, stickieId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static Widget getGroupWidget(int widgetId, int groupId) {
        Widget widget = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_stickers WHERE group_id = ? AND id = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, widgetId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                widget = fill(resultSet);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return widget;
    }

    public static Widget getInventoryWidget(int userId, int widgetId) {
        Widget widget = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_stickers WHERE id = ? AND user_id = ? AND is_placed = 0", sqlConnection);
            preparedStatement.setInt(1, widgetId);
            preparedStatement.setInt(2, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                widget = fill(resultSet);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return widget;
    }

    public static Widget getHomeWidget(int userId, int widgetId) {
        Widget widget = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_stickers WHERE id = ? AND user_id = ? AND group_id = 0", sqlConnection);
            preparedStatement.setInt(1, widgetId);
            preparedStatement.setInt(2, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                widget = fill(resultSet);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return widget;
    }

    public static Widget getWidget(int widgetId) {
        Widget widget = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM cms_stickers WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, widgetId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                widget = fill(resultSet);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return widget;
    }

    private static Widget fill(ResultSet resultSet) throws SQLException {
        return new Widget(resultSet.getInt("id"), resultSet.getInt("user_id"), resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"),
                resultSet.getInt("sticker_id"), resultSet.getInt("skin_id"), resultSet.getInt("group_id"), resultSet.getString("text"), 1, resultSet.getBoolean("is_placed"),
                resultSet.getString("extra_data"));
    }

}
