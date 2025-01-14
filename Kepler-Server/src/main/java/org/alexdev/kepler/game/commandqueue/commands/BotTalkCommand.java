package org.alexdev.kepler.game.commandqueue.commands;

import org.alexdev.kepler.game.bot.BotManager;
import org.alexdev.kepler.game.commandqueue.CommandTemplate;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;

public class BotTalkCommand implements Command {
    public void executeCommand(CommandTemplate commandArgs) {
        int roomId = commandArgs.RoomId;
        int botId = commandArgs.UserId;
        String message = commandArgs.Message;
        String type = commandArgs.Type;

        if(CHAT_MESSAGE.ChatMessageType.valueOf(type) == null) {
            return;
        }

        if (roomId == -1) {
            return;
        }

        Room room = RoomManager.getInstance().getRoomById(roomId);
        if (room == null) {
            return;
        }

        BotManager.getInstance().talk(roomId, botId, message, CHAT_MESSAGE.ChatMessageType.valueOf(type));
    }
}
