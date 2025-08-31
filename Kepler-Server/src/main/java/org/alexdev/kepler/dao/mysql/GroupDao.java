package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.groups.Group;

import java.sql.*;
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

    public static Group getGroupByAlias(String groupAlias) {
        Group group = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM groups_details WHERE alias = ?", sqlConnection);
            preparedStatement.setString(1, groupAlias);
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

    public static List<Group> getGroups(int userId) {
        List<Group> groupList = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM groups_details WHERE owner_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet =  preparedStatement.executeQuery();

            while (resultSet.next()) {
                groupList.add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return groupList;
    }

    public static List<Group> getJoinedGroups(int userId) {
        List<Group> groupList = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT " +
                    "groups_details.* " +
                    "FROM groups_memberships " +
                    "RIGHT JOIN " +
                    "groups_details ON groups_memberships.group_id = groups_details.id " +
                    "WHERE owner_id = ? " +
                    "OR (groups_memberships.user_id = ? AND groups_memberships.is_pending = 0)", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);
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

    public static int addGroup(String name, String description, int ownerId) {
        int groupId = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO groups_details (name, description, owner_id) VALUES (?, ?, ?)", sqlConnection);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, ownerId);
            preparedStatement.executeQuery();
            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                groupId = resultSet.getInt(1);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(preparedStatement);
        }

        return groupId;
    }

    public static int saveGroup(Group group) {
        int groupId = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE groups_details SET name = ?, description = ?, room_id = ?, badge = ?, recommended = ?, group_type = ?, forum_type = ?, forum_premission = ?, alias = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, group.getName());
            preparedStatement.setString(2, group.getDescription());
            preparedStatement.setInt(3, group.getRoomId());
            preparedStatement.setString(4, group.getBadge());
            preparedStatement.setInt(5, group.isRecommended() ? 1 : 0);
            preparedStatement.setInt(6, group.getGroupType());
            preparedStatement.setInt(7, group.getForumType().getId());
            preparedStatement.setInt(8, group.getForumPermission().getId());

            if (group.getAlias() == null || group.getAlias().isBlank()) {
                preparedStatement.setNull(9, Types.VARCHAR);
            }
            else {
                preparedStatement.setString(9, group.getAlias());
            }

            preparedStatement.setInt(10, group.getId());
            preparedStatement.executeQuery();
            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                groupId = resultSet.getInt(1);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(preparedStatement);
        }

        return groupId;
    }

    public static void saveBackground(Group group) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE groups_details SET background = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, group.getBackground());
            preparedStatement.setInt(2, group.getId());
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(preparedStatement);
        }
    }

    public static List<Group> querySearch(String query) {
        List<Group> groups = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM groups_details WHERE name LIKE ? LIMIT 30", sqlConnection);
            preparedStatement.setString(1, "%" + query + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                groups.add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return groups;
    }

    public static void saveBadge(Group group) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE groups_details SET badge = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, group.getBadge());
            preparedStatement.setInt(2, group.getId());
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(preparedStatement);
        }
    }

    public static String getGroupBadge(int groupId) {
        String group = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT badge FROM groups_details WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);
            resultSet =  preparedStatement.executeQuery();

            if (resultSet.next()) {
                group = resultSet.getString("badge");
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

    public static void delete(int groupId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM groups_details WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static boolean hasGroupByAlias(String url) {
        boolean group = false;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM groups_details WHERE alias = ?", sqlConnection);
            preparedStatement.setString(1, url);
            resultSet =  preparedStatement.executeQuery();

            if (resultSet.next()) {
                group = true;
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

    public static void deleteHomeRoom(int roomId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE groups_details SET room_id = 0 WHERE room_id = ?", sqlConnection);
            preparedStatement.setInt(1, roomId);
            preparedStatement.execute();

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static int getGroupOwner(int groupId) {
        int ownerId = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT owner_id FROM groups_details WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);
            resultSet =  preparedStatement.executeQuery();

            if (resultSet.next()) {
                ownerId = resultSet.getInt("owner_id");
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return ownerId;
    }

    public static String getGroupName(int groupId) {
        String groupName = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT name FROM groups_details WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);
            resultSet =  preparedStatement.executeQuery();

            if (resultSet.next()) {
                groupName = resultSet.getString("name");
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return groupName;
    }

    public static Group fill(ResultSet resultSet) throws SQLException {
        return new Group(resultSet.getInt("id"),  resultSet.getString("name"), resultSet.getString("description"), resultSet.getInt("owner_id"),
                resultSet.getInt("room_id"), resultSet.getString("badge"), resultSet.getBoolean("recommended"), resultSet.getString("background"), resultSet.getInt("views"),
                resultSet.getInt("topics"), resultSet.getInt("group_type"), resultSet.getInt("forum_type"), resultSet.getInt("forum_premission"),
                resultSet.getString("alias"), resultSet.getTime("created_at").getTime() / 1000L);
    }
}
