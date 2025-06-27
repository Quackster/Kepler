package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.groups.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GroupDao {
    public static Group getGroup(int groupId) {
        Group group = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM groups_details WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);
            resultSet =  preparedStatement.executeQuery();

            if (resultSet.next()) {
                group = fill(resultSet);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return group;
    }

    public static List<Group> getJoinedGroups(int userId) {
        List<Group> groupList = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM groups_memberships " +
                    "JOIN groups_details ON group_id = id " +
                    "WHERE user_id = ? AND is_pending = 0", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int groupId = resultSet.getInt("id");

                if (groupList.stream().noneMatch(group -> group.getId() == groupId)) {
                    groupList.add(fill(resultSet));
                }
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return groupList.stream()
                .sorted(Comparator.comparingInt((Group group) -> group.getMemberCount(false)).reversed())
                .collect(Collectors.toList());
    }

    public static Group fill(ResultSet resultSet) throws SQLException {
        return new Group(resultSet.getInt("id"),  resultSet.getString("name"), resultSet.getString("description"),
                resultSet.getInt("owner_id"), resultSet.getString("badge"));
    }
}
