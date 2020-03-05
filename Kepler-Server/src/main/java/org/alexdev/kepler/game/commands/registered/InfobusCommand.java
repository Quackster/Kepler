package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.infobus.Infobus;
import org.alexdev.kepler.game.infobus.InfobusManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.outgoing.rooms.infobus.POLL_QUESTION;
import org.alexdev.kepler.messages.outgoing.rooms.items.SHOWPROGRAM;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.util.StringUtil;

public class InfobusCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.ADMINISTRATOR_ACCESS);
    }

    @Override
    public void addArguments() {
        this.arguments.add("question");
        //this.arguments.add("options");
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        String cmd = args[0];



        Room room = player.getRoomUser().getRoom();
        Infobus bus = InfobusManager.getInstance().bus();

        if(bus == null) {
            return;
        }
        if(cmd.equalsIgnoreCase("status")) {
            player.send(new ALERT("PLAYERS: " + bus.getPlayers().toString() + "\r" + "ACTIVE: " + "\r" + bus.getPollActive()));
        }

        if(cmd.equalsIgnoreCase("stoppoll")) {
            bus.stopPoll();
        }

        if(cmd.equalsIgnoreCase("startpoll")) {
            bus.startPoll();
        }

        if(cmd.equalsIgnoreCase("close")) {
            bus.closeDoor();
        }

        if(cmd.equalsIgnoreCase("open")) {
            bus.openDoor();
        }


        /*


        String[] options = args[1].split(",");
        String optionsString = new String();
        for (String option: options) {
            optionsString.concat(option + "\r ");
        }

        String alert = StringUtil.filterInput(String.join(" ", args), true);

        String infobusPoll = args[0] + "\r" + args[1];

        */



    }

    @Override
    public String getDescription() {
        return "<question> <options> - Options seperated by comma fx 'no,yes'";
    }
}