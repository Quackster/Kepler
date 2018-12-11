package org.alexdev.roseau.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.dao.NavigatorDao;
import org.alexdev.roseau.dao.util.IProcessStorage;
import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.log.Log;

import com.google.common.collect.Lists;

public class MySQLNavigatorDao extends IProcessStorage<Room, ResultSet> implements NavigatorDao {

    private MySQLDao dao;

    public MySQLNavigatorDao(MySQLDao dao) {
        this.dao = dao;
    }

    @Override
    public List<Room> getRoomsByLikeName(String name) {

        List<Room> rooms = Lists.newArrayList();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = this.dao.getStorage().getConnection();
            preparedStatement = this.dao.getStorage().prepare("SELECT * FROM rooms WHERE name LIKE ? AND room_type = 0", sqlConnection);
            preparedStatement.setString(1, "%" + name + "%");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                int id = resultSet.getInt("id");

                Room room = Roseau.getGame().getRoomManager().getRoomByID(id);

                if (room == null) {
                    room = this.fill(resultSet);
                }

                rooms.add(room);
            }

        } catch (Exception e) {
            Log.exception(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return rooms;
    }

    @Override
    public Room fill(ResultSet row) throws Exception {

        RoomType type = RoomType.getType(row.getInt("room_type"));

        PlayerDetails details = null;

        if (type == RoomType.PRIVATE) {
            details = Roseau.getDao().getPlayer().getDetails(row.getInt("owner_id"));
        }

        Room instance = new Room();

        instance.getData().fill(row.getInt("id"), (row.getInt("hidden") == 1), type, details == null ? 0 : details.getID(), details == null ? "" : details.getName(), row.getString("name"), 
                row.getInt("state"), row.getString("password"), row.getInt("users_now"), row.getInt("users_max"), row.getString("description"), row.getString("model"),
                row.getString("cct"), row.getString("wallpaper"), row.getString("floor"), false, row.getInt("show_owner_name") == 1);

        if (details != null) {
            instance.getData().setOwnerName(details.getName());
        }

        instance.load();
        return instance;
    }
}
