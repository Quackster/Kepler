package org.alexdev.kepler.game.commandqueue.commands;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.commandqueue.CommandTemplate;
import org.alexdev.kepler.game.moderation.actions.ModeratorKickUserAction;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.outgoing.messenger.ROOMFORWARD;

public class RoomForwardCommand implements Command {
    public void executeCommand(CommandTemplate commandArgs) {
        boolean publicRoom = commandArgs.RoomType.equalsIgnoreCase("public");
        int roomId = commandArgs.RoomId;
        Player player = PlayerManager.getInstance().getPlayerById(commandArgs.UserId);
        if(player == null) return;

        // Because public rooms sucks right!?
        if(publicRoom) {
            roomId = roomId - RoomManager.PUBLIC_ROOM_OFFSET;
        }

        Room room = RoomManager.getInstance().getRoomById(roomId);
        if(room == null) return;
        player.send(new ROOMFORWARD(publicRoom, roomId));
    }
}
