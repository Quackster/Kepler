package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.commandqueue.CommandQueue;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.room.RoomData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommandQueueDao {

    /**
     * Get a list of users with room rights for a room
     *
     * @return the list of user ids who have room rights
     */
    public static List<CommandQueue> getNotYetExecutedCommands() {
        List<CommandQueue> commands = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT id, executed, command, arguments FROM command_queue where executed = '0'", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                commands.add(new CommandQueue(resultSet.getInt("id"), resultSet.getBoolean("executed"), resultSet.getString("command"), resultSet.getString("arguments")));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return commands;
    }

    public static void setExecuted(CommandQueue queue) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE command_queue set executed = '1' where id=?", sqlConnection);
            preparedStatement.setInt(1, queue.getId());
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

}
