package net.h4bbo.kepler.dao.mysql;


import net.h4bbo.kepler.dao.Storage;
import net.h4bbo.kepler.game.groups.GroupMember;
import net.h4bbo.kepler.game.groups.GroupMemberRank;
import net.h4bbo.kepler.game.player.PlayerDetails;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupMemberDao {
    /*public static List<GroupMember> getMembers(int groupId, boolean checkPending) {
        List<GroupMember> members = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM groups_memberships WHERE group_id = ? AND is_pending = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, checkPending ? 1 : 0);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                members.add(fill(resultSet));//.getInt("user_id"));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return members;
    }*/

    public static List<GroupMember> getMembers(int groupId, boolean checkPending, String query, int page, int itemsPerPage) {
        List<GroupMember> members = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            if (!query.isBlank()) {
                preparedStatement = Storage.getStorage().prepare("SELECT * FROM groups_memberships INNER JOIN users ON groups_memberships.user_id = users.id WHERE group_id = ? AND is_pending = ? AND username LIKE ? LIMIT " + ((page - 1) * itemsPerPage) + "," + itemsPerPage, sqlConnection);
                preparedStatement.setInt(1, groupId);
                preparedStatement.setInt(2, checkPending ? 1 : 0);
                preparedStatement.setString(3, query + "%");
            } else {
                preparedStatement = Storage.getStorage().prepare("SELECT * FROM groups_memberships INNER JOIN users ON groups_memberships.user_id = users.id WHERE group_id = ? AND is_pending = ? LIMIT " + ((page - 1) * itemsPerPage) + "," + itemsPerPage, sqlConnection);
                preparedStatement.setInt(1, groupId);
                preparedStatement.setInt(2, checkPending ? 1 : 0);
            }
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                members.add(fill(resultSet));//.getInt("user_id"));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return members;
    }
    

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

    public static void addMember(int userId, int groupId, boolean insertPending) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO groups_memberships (user_id, group_id, is_pending) VALUES (?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, groupId);
            preparedStatement.setLong(3, insertPending ? 1 : 0);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void updateMember(int userId, int groupId, GroupMemberRank memberRank, boolean pendingStatus) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE groups_memberships SET is_pending = ?, member_rank = ? WHERE user_id = ? AND group_id = ?", sqlConnection);
            preparedStatement.setInt(1, pendingStatus ? 1 : 0);
            preparedStatement.setString(2, String.valueOf(memberRank.getRankId()));
            preparedStatement.setInt(3, userId);
            preparedStatement.setInt(4, groupId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void deleteMember(int userId, int groupId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM groups_memberships WHERE user_id = ? AND group_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, groupId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static Pair<Integer, Map<String, String>> getPendingMembers(int userId) {
        var groupData = new HashMap<String, String>();
        var groups = new HashMap<String, String>();
        int pendingMembers = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT " +
                    "groups_details.id AS group_id, " +
                    "groups_details.name AS group_name " +
                    "FROM groups_memberships " +
                    "RIGHT JOIN " +
                    "groups_details ON groups_memberships.group_id = groups_details.id " +
                    "WHERE owner_id = ? " +
                    "OR (groups_memberships.user_id = ? AND (groups_memberships.member_rank = '2' OR groups_memberships.member_rank = '3'))", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int groupId = resultSet.getInt("group_id");
                String groupName = resultSet.getString("group_name");
                groupData.put(String.valueOf(groupId), groupName);
            }

            if (groupData.size() > 0) {
                preparedStatement = Storage.getStorage().prepare("SELECT " +
                        "user_id, group_id " +
                        "FROM groups_memberships " +
                        "WHERE group_id IN (" + String.join(",", groupData.keySet()) + ") " +
                        "AND is_pending = 1 GROUP BY group_id", sqlConnection);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int groupId = resultSet.getInt("group_id");

                    if (!groups.containsKey(String.valueOf(groupId))) {
                        groups.put(String.valueOf(groupId), groupData.get(String.valueOf(groupId)));
                    }

                    pendingMembers++;
                }
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return Pair.of(pendingMembers, groups);
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

    public static List<PlayerDetails> getOnlineMembersByFavourite(int groupId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<PlayerDetails> detailsList = new ArrayList<PlayerDetails>();

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM users WHERE favourite_group = ? AND is_online = 1 LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, groupId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                PlayerDetails details = new PlayerDetails();
                PlayerDao.fill(details, resultSet);

                detailsList.add(details);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return detailsList;
    }

    public static void resetFavourites(int groupId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE users SET favourite_group = 0 WHERE favourite_group = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }


    public static void deleteMembers(int groupId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {

            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM groups_memberships WHERE group_id = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    private static GroupMember fill(ResultSet resultSet) throws SQLException {
        return new GroupMember(resultSet.getInt("user_id"), resultSet.getInt("group_id"), resultSet.getBoolean("is_pending"), Integer.parseInt(resultSet.getString("member_rank")));
    }
}
