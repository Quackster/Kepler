package net.h4bbo.http.kepler.dao;

import net.h4bbo.kepler.dao.Storage;
import net.h4bbo.http.kepler.game.groups.DiscussionReply;
import net.h4bbo.http.kepler.game.groups.DiscussionTopic;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDiscussionDao {
    public static final int MAX_UNREAD_DAYS = 7;

    public static Pair<Integer, Map<String, String>> getNewGroupMessages(int userId, long lastOnline) {
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
                    "WHERE (owner_id = ? " +
                    "OR (groups_memberships.user_id = ? AND groups_memberships.is_pending = 0 AND (groups_memberships.member_rank = '1' OR groups_memberships.member_rank = '2' OR groups_memberships.member_rank = '3')))", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int groupId = resultSet.getInt("group_id");
                String groupName = resultSet.getString("group_name");
                groupData.put(String.valueOf(groupId), groupName);
            }

            if (groupData.size() > 0) {
                preparedStatement = Storage.getStorage().prepare("SELECT group_id " +
                        "FROM cms_forum_replies " +
                        "INNER JOIN cms_forum_threads ON cms_forum_threads.id = cms_forum_replies.thread_id " +
                        "WHERE " +
                        "cms_forum_threads.group_id IN (" + String.join(",", groupData.keySet()) + ") " +
                        "AND (DATEDIFF(NOW(), cms_forum_replies.created_at) <= " + GroupDiscussionDao.MAX_UNREAD_DAYS + ") " +
                        "AND NOT EXISTS (SELECT * FROM cms_forums_read_replies WHERE cms_forums_read_replies.reply_id = (SELECT MAX(id) FROM cms_forum_replies WHERE cms_forum_replies.thread_id = cms_forum_threads.id)" +
                        " AND cms_forums_read_replies.user_id = " + userId + ") " +
                        "GROUP BY group_id", sqlConnection);
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

    public static List<DiscussionTopic> getDiscussions(int groupId, int page, int itemsPerPage, int userId) {
        List<DiscussionTopic> discussionList = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT " +
                    "cms_forum_threads.*, " +
                    "cms_forum_replies.created_at AS last_message_at, " +
                    "cms_forum_replies.id AS last_reply_id, " +
                    "creator.username AS creator_name, " +
                    "creator.id AS creator_id, " +
                    "replier.username AS last_reply_name, " +
                    "(SELECT COUNT(*) FROM cms_forum_replies WHERE cms_forum_replies.thread_id = cms_forum_threads.id) AS reply_count, " +
                    (userId > 0 ? "(SELECT COUNT(*) FROM cms_forums_read_replies WHERE (cms_forums_read_replies.reply_id = last_reply_id AND cms_forums_read_replies.user_id = " + userId + ") OR " +
                            " (DATEDIFF(NOW(), cms_forum_replies.created_at) > " + GroupDiscussionDao.MAX_UNREAD_DAYS + ")) AS has_read " : "0 as has_read ") +
                    "FROM " +
                    "cms_forum_replies " +
                    "INNER JOIN cms_forum_threads ON cms_forum_threads.id = cms_forum_replies.thread_id " +
                    "INNER JOIN users replier ON cms_forum_replies.poster_id = replier.id " +
                    "INNER JOIN users creator ON cms_forum_threads.poster_id = creator.id " +
                    "WHERE " +
                    "cms_forum_replies.id = (SELECT MAX(id) FROM cms_forum_replies WHERE cms_forum_replies.thread_id = cms_forum_threads.id) AND " +
                    "group_id = ? " +
                    "ORDER BY " +
                    "is_stickied DESC, " +
                    "cms_forum_replies.created_at DESC " +
                    "LIMIT " + ((page - 1) * itemsPerPage) + "," + itemsPerPage, sqlConnection);
            preparedStatement.setInt(1, groupId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                discussionList.add(fill(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return discussionList;
    }

    public static int countDiscussions(int groupId) {
        int discussions = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT COUNT(*) AS discussion_count FROM cms_forum_threads WHERE cms_forum_threads.group_id = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                discussions = resultSet.getInt("discussion_count");
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return discussions;
    }

    public static List<DiscussionReply> getReplies(int groupId, int page, int itemsPerPage, int userId) {
        List<DiscussionReply> replyList = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT   " +
                    "cms_forum_replies.*, " +
                    "users.id AS user_id, " +
                    "users.figure AS figure, " +
                    "users.username AS username, " +
                    "users.is_online AS is_online, " +
                    "IFNULL(users.favourite_group, 0) as group_id, " +
                    "(SELECT users_badges.badge FROM users_badges WHERE users_badges.user_id = users.id AND users_badges.equipped ORDER BY slot_id ASC LIMIT 1) AS equipped_badge, " +
                    "(SELECT groups_details.badge FROM groups_details WHERE groups_details.id = users.favourite_group) AS group_badge, " +
                    "(SELECT COUNT(*) FROM cms_forum_replies WHERE cms_forum_replies.poster_id = users.id) AS forum_messages, " +
                    (userId > 0 ? "(SELECT COUNT(*) FROM cms_forums_read_replies WHERE (cms_forums_read_replies.reply_id = cms_forum_replies.id AND cms_forums_read_replies.user_id = " + userId + ") OR " +
                            " (DATEDIFF(NOW(), cms_forum_replies.created_at) > " + GroupDiscussionDao.MAX_UNREAD_DAYS + ")) AS has_read " : "0 as has_read ") +
                    "FROM  " +
                    "cms_forum_replies " +
                    "INNER JOIN users ON users.id = cms_forum_replies.poster_id " +
                    "WHERE  " +
                    "thread_id = ? " +
                    "ORDER BY  " +
                    "cms_forum_replies.created_at  " +
                    "ASC LIMIT " + ((page - 1) * itemsPerPage) + "," + itemsPerPage, sqlConnection);
            preparedStatement.setInt(1, groupId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                replyList.add(fillReply(resultSet));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return replyList;
    }

    public static int getFirstReply(int discussionId) {
        int id = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT id FROM cms_forum_replies WHERE thread_id = ? ORDER BY created_at ASC LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, discussionId);
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return id;
    }

    public static int countReplies(int groupId) {
        int replies = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT   " +
                    "COUNT(*) AS replies " +
                    "FROM  " +
                    "cms_forum_replies " +
                    "INNER JOIN users ON users.id = cms_forum_replies.poster_id " +
                    "WHERE " +
                    "thread_id = ? " +
                    "ORDER BY " +
                    "cms_forum_replies.created_at " +
                    "ASC", sqlConnection);
            preparedStatement.setInt(1, groupId);
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                replies = resultSet.getInt("replies");
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return replies;
    }

    public static int createDiscussion(int groupId, int posterId, String topicTitle) {
        int id = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO cms_forum_threads (topic_title, poster_id, group_id) VALUES (?, ?, ?)", sqlConnection);
            preparedStatement.setString(1, topicTitle);
            preparedStatement.setInt(2, posterId);
            preparedStatement.setInt(3, groupId);
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet != null && resultSet.next()) {
                id = resultSet.getInt(1);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return id;
    }

    public static DiscussionTopic getDiscussion(int groupId, int discussionId, int userId) {
        DiscussionTopic topic = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT " +
                    "cms_forum_threads.*, " +
                    "cms_forum_replies.created_at AS last_message_at, " +
                    "cms_forum_replies.id AS last_reply_id, " +
                    "creator.username AS creator_name, " +
                    "creator.id AS creator_id, " +
                    "replier.username AS last_reply_name, " +
                    "(SELECT COUNT(*) FROM cms_forum_replies WHERE cms_forum_replies.thread_id = cms_forum_threads.id) AS reply_count, " +
                    (userId > 0 ? "(SELECT COUNT(*) FROM cms_forums_read_replies WHERE (cms_forums_read_replies.reply_id = last_reply_id AND cms_forums_read_replies.user_id = " + userId + ") OR " +
                            " (DATEDIFF(NOW(), cms_forum_replies.created_at) > " + GroupDiscussionDao.MAX_UNREAD_DAYS + ")) AS has_read " : "0 as has_read ") +
                    "FROM " +
                    "cms_forum_replies " +
                    "INNER JOIN cms_forum_threads ON cms_forum_threads.id = cms_forum_replies.thread_id " +
                    "INNER JOIN users replier ON cms_forum_replies.poster_id = replier.id " +
                    "INNER JOIN users creator ON cms_forum_threads.poster_id = creator.id " +
                    "WHERE " +
                    "cms_forum_threads.group_id = ? " +
                    "AND cms_forum_threads.id = ? " +
                    "ORDER BY " +
                    "cms_forum_replies.created_at " +
                    "DESC " +
                    "LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, discussionId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                topic = fill(resultSet);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return topic;
    }

    public static DiscussionReply getReply(int discussionId, int replyId, int userId) {
        DiscussionReply reply = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT " +
                    "cms_forum_replies.*, " +
                    "users.id AS user_id, " +
                    "users.figure AS figure, " +
                    "users.username AS username, " +
                    "users.is_online AS is_online, " +
                    "IFNULL(users.favourite_group, 0) as group_id, " +
                    "(SELECT users_badges.badge FROM users_badges WHERE users_badges.user_id = users.id AND users_badges.equipped ORDER BY slot_id ASC LIMIT 1) AS equipped_badge, " +
                    "(SELECT groups_details.badge FROM groups_details WHERE groups_details.id = users.favourite_group) AS group_badge, " +
                    "(SELECT COUNT(*) FROM cms_forum_replies WHERE cms_forum_replies.poster_id = users.id) AS forum_messages, " +
                    (userId > 0 ? "(SELECT COUNT(*) FROM cms_forums_read_replies WHERE (cms_forums_read_replies.reply_id = cms_forum_replies.id AND cms_forums_read_replies.user_id = " + userId + ") OR " +
                            " (DATEDIFF(NOW(), cms_forum_replies.created_at) > " + GroupDiscussionDao.MAX_UNREAD_DAYS + ")) AS has_read " : "0 as has_read ") +
                    "FROM " +
                    "cms_forum_replies " +
                    "INNER JOIN users ON users.id = cms_forum_replies.poster_id " +
                    "WHERE " +
                    "thread_id = ? AND cms_forum_replies.id = ? LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, discussionId);
            preparedStatement.setInt(2, replyId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                reply = fillReply(resultSet);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return reply;
    }

    public static DiscussionReply getLatestReply(int userId) {
        DiscussionReply reply = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT " +
                    "cms_forum_replies.*, " +
                    "users.id AS user_id, " +
                    "users.figure AS figure, " +
                    "users.username AS username, " +
                    "users.is_online AS is_online, " +
                    "IFNULL(users.favourite_group, 0) as group_id, " +
                    "(SELECT users_badges.badge FROM users_badges WHERE users_badges.user_id = users.id AND users_badges.equipped ORDER BY slot_id ASC LIMIT 1) AS equipped_badge, " +
                    "(SELECT groups_details.badge FROM groups_details WHERE groups_details.id = users.favourite_group) AS group_badge, " +
                    "(SELECT COUNT(*) FROM cms_forum_replies WHERE cms_forum_replies.poster_id = users.id) AS forum_messages, " +
                    (userId > 0 ? "(SELECT COUNT(*) FROM cms_forums_read_replies WHERE (cms_forums_read_replies.reply_id = cms_forum_replies.id AND cms_forums_read_replies.user_id = " + userId + ") OR " +
                            " (DATEDIFF(NOW(), cms_forum_replies.created_at) > " + GroupDiscussionDao.MAX_UNREAD_DAYS + ")) AS has_read " : "0 as has_read ") +
                    "FROM " +
                    "cms_forum_replies " +
                    "INNER JOIN users ON users.id = cms_forum_replies.poster_id " +
                    "WHERE " +
                    "poster_id = ? ORDER BY created_at DESC LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                reply = fillReply(resultSet);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return reply;
    }

    public static int countUserReplies(int userId) {
        int replies = 0;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT COUNT(*) FROM cms_forum_replies WHERE cms_forum_replies.poster_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                replies = resultSet.getInt(1);
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return replies;
    }

    public static String[] getDisplayBadges(int userId) {
        String[] badges = new String[2];

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT " +
                    "(SELECT users_badges.badge FROM users_badges WHERE users_badges.user_id = users.id AND users_badges.equipped ORDER BY slot_id ASC LIMIT 1) AS equipped_badge, " +
                    "(SELECT groups_details.badge FROM groups_details WHERE groups_details.id = users.favourite_group) AS group_badge " +
                    "FROM " +
                    "users " +
                    "WHERE " +
                    "users.id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                badges[0] = resultSet.getString("equipped_badge");
                badges[1] = resultSet.getString("group_badge");
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return badges;
    }

    public static void createReplies(int threadId, int posterId, String message) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO cms_forum_replies (thread_id, message, poster_id) VALUES (?, ?, ?)", sqlConnection);
            preparedStatement.setInt(1, threadId);
            preparedStatement.setString(2, message);
            preparedStatement.setInt(3, posterId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void deleteDiscussion(int groupId, int topicId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM cms_forum_threads WHERE id = ? AND group_id = ?", sqlConnection);
            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, topicId);
            preparedStatement.execute();

            preparedStatement = Storage.getStorage().prepare("DELETE FROM cms_forum_replies WHERE thread_id = ?", sqlConnection);
            preparedStatement.setInt(1, topicId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void saveDiscussion(DiscussionTopic discussionTopic) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE cms_forum_threads SET topic_title = ?, is_open = ?, is_stickied = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, discussionTopic.getTopicTitle());
            preparedStatement.setBoolean(2, discussionTopic.isOpen());
            preparedStatement.setBoolean(3, discussionTopic.isStickied());
            preparedStatement.setInt(4, discussionTopic.getId());
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void saveReply(DiscussionReply discussionReply) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE cms_forum_replies SET is_deleted = ?, is_edited = ?, message = ? WHERE id = ?", sqlConnection);
            preparedStatement.setBoolean(1, discussionReply.isDeleted());
            preparedStatement.setBoolean(2, discussionReply.isEdited());
            preparedStatement.setString(3, discussionReply.getMessage());
            preparedStatement.setInt(4, discussionReply.getId());
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void deleteReply(DiscussionReply discussionReply) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM cms_forum_replies WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, discussionReply.getId());
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void incrementViews(int discussionId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE cms_forum_threads SET views = views + 1 WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1,  discussionId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static DiscussionTopic fill(ResultSet resultSet) throws SQLException {
        return new DiscussionTopic(resultSet.getInt("id"), resultSet.getInt("group_id"),
                resultSet.getString("topic_title"),
                resultSet.getInt("reply_count"), resultSet.getBoolean("is_open"),
                resultSet.getBoolean("is_stickied"), resultSet.getInt("views"),
                resultSet.getTime("created_at"), resultSet.getTime("last_message_at"),
                resultSet.getInt("creator_id"),
                resultSet.getString("creator_name"), resultSet.getString("last_reply_name"), resultSet.getInt("has_read") != 0);
    }

    private static DiscussionReply fillReply(ResultSet resultSet) throws SQLException {
        return new DiscussionReply(resultSet.getInt("id"), resultSet.getInt("user_id"),
                resultSet.getString("message"), resultSet.getString("figure"),
                resultSet.getString("username"), resultSet.getBoolean("is_online"), resultSet.getString("equipped_badge"),
                resultSet.getInt("group_id"),  resultSet.getString("group_badge"),
                resultSet.getInt("forum_messages"),
                resultSet.getBoolean("is_edited"), resultSet.getBoolean("is_deleted"),
                resultSet.getTime("created_at"), resultSet.getTime("modified_at"), resultSet.getInt("has_read") != 0);
    }
}
