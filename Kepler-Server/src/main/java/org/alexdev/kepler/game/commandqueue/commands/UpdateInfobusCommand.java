package org.alexdev.kepler.game.commandqueue.commands;

import org.alexdev.kepler.game.commandqueue.CommandTemplate;
import org.alexdev.kepler.game.infobus.InfobusManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;

public class UpdateInfobusCommand implements Command {
    public void executeCommand(CommandTemplate commandArgs) {
        if(commandArgs.Type.equalsIgnoreCase("setQuestion"))   {
            InfobusManager.getInstance().setQuestion(commandArgs.Message);
        } else if(commandArgs.Type.equalsIgnoreCase("addOption")) {
            InfobusManager.getInstance().addOption(commandArgs.Message);
        } else if(commandArgs.Type.equalsIgnoreCase("removeOption")) {
            InfobusManager.getInstance().removeOption(Integer.parseInt(commandArgs.Message));
        } else if(commandArgs.Type.equalsIgnoreCase("open")) {
            Room room = RoomManager.getInstance().getRoomByModel("park_a");
            InfobusManager.getInstance().openDoor(room);
        } else if(commandArgs.Type.equalsIgnoreCase("close")) {
            Room room = RoomManager.getInstance().getRoomByModel("park_a");
            InfobusManager.getInstance().closeDoor(room);
        } else if(commandArgs.Type.equalsIgnoreCase("reset")) {
            InfobusManager.getInstance().reset();
        } else if(commandArgs.Type.equalsIgnoreCase("start")) {
            InfobusManager.getInstance().startPoll();
        } else if(commandArgs.Type.equalsIgnoreCase("status")) {
            InfobusManager.getInstance().SendStatusToQueue();
        }
    }
}
