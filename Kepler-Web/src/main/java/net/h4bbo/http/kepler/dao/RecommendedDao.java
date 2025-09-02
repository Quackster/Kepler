package net.h4bbo.http.kepler.dao;

import net.h4bbo.kepler.dao.Storage;
import net.h4bbo.kepler.dao.mysql.GroupDao;
import net.h4bbo.kepler.game.groups.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RecommendedDao {
    public static List<Group> getRecommendedGroups(boolean staffPick) {
        List<Group> groupList = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT groups_details.* FROM cms_recommended INNER JOIN groups_details ON cms_recommended.recommended_id = groups_details.id WHERE type = 'GROUP' AND is_staff_pick = ?", sqlConnection);
            preparedStatement.setInt(1, staffPick ? 1 : 0);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                groupList.add(GroupDao.fill(resultSet));
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
}
