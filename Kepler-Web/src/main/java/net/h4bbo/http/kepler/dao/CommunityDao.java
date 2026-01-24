package net.h4bbo.http.kepler.dao;

import net.h4bbo.kepler.dao.Storage;
import net.h4bbo.kepler.dao.mysql.GroupDao;
import net.h4bbo.kepler.game.groups.Group;
import net.h4bbo.http.kepler.game.groups.DiscussionTopic;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityDao {
    public static Map<Group, Integer> getHotGroups(int limit, int offset) {
        Map<Group, Integer> hotGroups = new HashMap<>();
        // SELECT group_id, COUNT(group_id) AS popularity FROM groups_memberships WHERE (UNIX_TIMESTAMP(created_at) + 2592000) > UNIX_TIMESTAMP() GROUP BY group_id

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT *, " +
                    "(SELECT COUNT(*) FROM groups_memberships " +
                    "WHERE group_id = id " +
                    "AND (groups_memberships.created_at between (CURDATE() - INTERVAL 1 MONTH ) and CURDATE())) AS popularity " +
                    "FROM groups_details " +
                    "WHERE groups_details.created_at between (CURDATE() - INTERVAL 1 MONTH) and CURDATE()" +
                    "LIMIT " + limit + " OFFSET " + offset, sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                hotGroups.put(GroupDao.fill(resultSet), resultSet.getInt("popularity"));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return hotGroups;
    }

    public static List<DiscussionTopic> getRecentDiscussions(int limit, int offset) {
        List<DiscussionTopic> discussionList = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT DISTINCT " +
                    "cms_forum_threads.*, " +
                    "cms_forum_replies.created_at AS last_message_at, " +
                    "cms_forum_replies.id AS last_reply_id, " +
                    /*
                    "creator.username AS creator_name, " +
                    "creator.id AS creator_id, " +
                    "replier.username AS last_reply_name, " +
                    */
                    "'' AS creator_name, " +
                    "0 AS creator_id, " +
                    "'' AS last_reply_name, " +
                    "(SELECT COUNT(*) FROM cms_forum_replies WHERE cms_forum_replies.thread_id = cms_forum_threads.id) AS reply_count, " +
                    /*(userId > 0 ? "(SELECT COUNT(*) FROM cms_forums_read_replies WHERE cms_forums_read_replies.reply_id = last_reply_id AND cms_forums_read_replies.user_id = " + userId + " AND " +
                            "last_message_at < UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL " + GroupDiscussionDao.MAX_UNREAD_DAYS + " DAY))) AS has_read " : "0 as has_read ") +*/
                    "0 as has_read " +
                    "FROM " +
                    "cms_forum_replies " +
                    "INNER JOIN cms_forum_threads ON cms_forum_threads.id = cms_forum_replies.thread_id " +
                    //"INNER JOIN users replier ON cms_forum_replies.poster_id = replier.id " +
                    //"INNER JOIN users creator ON cms_forum_threads.poster_id = creator.id " +
                    "WHERE " +
                    "cms_forum_replies.id = (SELECT MAX(id) FROM cms_forum_replies WHERE cms_forum_replies.thread_id = cms_forum_threads.id) " +
                    "ORDER BY " +
                    //"is_stickied DESC, " +
                    "cms_forum_replies.created_at DESC " +
                    "LIMIT " + limit + " OFFSET " + offset, sqlConnection);
            /*preparedStatement = Storage.getStorage().prepare("SELECT DISTINCT " +
                    "cms_forum_threads.*, " +
                    "NOW() AS last_message_at, " +
                    "0 AS last_reply_id, " +
                    "'null' AS creator_name, " +
                    "0 AS creator_id, " +
                    "'null' AS last_reply_name, " +
                    "(SELECT COUNT(*) FROM cms_forum_replies WHERE cms_forum_replies.thread_id = cms_forum_threads.id) AS reply_count, " +
                    "0 as has_read " +
                    "FROM " +
                    "cms_forum_threads " +
                    "ORDER BY " +
                    "created_at DESC " +
                    "LIMIT " + limit + " OFFSET " + offset, sqlConnection);*/
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                discussionList.add(GroupDiscussionDao.fill(resultSet));
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
}
