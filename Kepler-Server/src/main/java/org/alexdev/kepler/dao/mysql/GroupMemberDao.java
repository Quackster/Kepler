package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.groups.GroupMember;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class GroupMemberDao {
    public static GroupMember getMember(int groupId, int userId) {
        GroupMember member = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM groups_memberships WHERE group_id = ? AND user_id = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                member = fill(resultSet);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return member;
    }

    public static int countMembers(int groupId, boolean isPending) {
        int count = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT COUNT(*) AS member_count FROM groups_memberships WHERE group_id = ? AND is_pending = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, isPending ? 1 : 0);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt("member_count");
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return count;
    }


    private static GroupMember fill(ResultSet resultSet) throws SQLException {
        return new GroupMember(resultSet.getInt("user_id"), resultSet.getInt("group_id"), resultSet.getBoolean("is_pending"), Integer.parseInt(resultSet.getString("member_rank")));
    }
}