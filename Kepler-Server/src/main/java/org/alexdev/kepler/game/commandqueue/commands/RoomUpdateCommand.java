package org.alexdev.kepler.game.commandqueue.commands;

import org.alexdev.kepler.dao.mysql.RoomDao;
import org.alexdev.kepler.game.commandqueue.CommandTemplate;
import org.alexdev.kepler.game.navigator.NavigatorManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.outgoing.messenger.ROOMFORWARD;

public class RoomUpdateCommand implements Command {
    public void executeCommand(CommandTemplate commandArgs) {
        var room = RoomDao.getRoomById(commandArgs.RoomId);
        if(room != null) {

            room.getData().setName(commandArgs.RoomName);
            room.getData().setDescription(commandArgs.RoomDescription);
            room.getData().setShowOwnerName(commandArgs.RoomShowOwnerName);
            room.getData().setAccessType(commandArgs.RoomAccessType);

            RoomDao.save(room);

            NavigatorManager.getInstance().resetCategoryMap();
        }
    }
}
