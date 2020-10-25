package org.alexdev.kepler.dao.mysql;

import com.goterl.lazycode.lazysodium.LazySodiumJava;
import com.goterl.lazycode.lazysodium.SodiumJava;
import com.goterl.lazycode.lazysodium.interfaces.PwHash;
import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.group.Group;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.tag.Tag;
import org.alexdev.kepler.util.DateUtil;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class GroupDao {

    /**
     * Get the memberstatus integer
     *
     * @param userId userId of the user
     * @param groupId Group Id
     * @return the status as int for the client
     */
    public static int getMemberStatus(int groupId, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int status = 0;


        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT  \tg.id AS group_id, g.user_id AS owner_id, (SELECT m.rights\n" +
                    "FROM     group_members as m\n" +
                    "WHERE    m.group_id = ? AND m.user_id = ?) AS rights\n" +
                    "FROM     cms_homes as g\n" +
                    "WHERE    g.id = ? LIMIT 1\n", sqlConnection);

            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, groupId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                if(resultSet.getInt("rights") == 1) {
                    status = 2;
                } else if(resultSet.getInt("owner_id") == userId) {
                    status = 1;
                } else {
                    status = 3;
                }
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
        System.out.println(status);
        return status;
    }

    /**
     * Gets the group
     *
     * @param groupId Group Id
     * @return the status as int for the client
     */
    public static Group getGroup(int groupId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Group group = null;


        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * from cms_homes WHERE id = ? LIMIT 1 ", sqlConnection);

            preparedStatement.setInt(1, groupId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                 group = new Group(resultSet.getInt("id"), resultSet.getString("group_badge"), resultSet.getString("group_name"), resultSet.getString("group_description"));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return group;
    }

}
