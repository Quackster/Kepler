package org.alexdev.kepler.game.commandqueue.commands;

import org.alexdev.kepler.game.bot.Bot;
import org.alexdev.kepler.game.bot.BotManager;
import org.alexdev.kepler.game.commandqueue.CommandTemplate;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;

public class ResetBotCommand implements Command {
    public void executeCommand(CommandTemplate commandArgs) {
        int roomId = commandArgs.RoomId;

        if (roomId == -1) {
            return;
        }

        Room room = RoomManager.getInstance().getRoomById(roomId);
        if (room == null) {
            return;
        }
        try {
            if(room.isActive() != true) {
                return;
            }
            BotManager.getInstance().resetBots(room);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
