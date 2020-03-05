package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.infobus.InfobusManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.util.StringUtil;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.stream.IntStream;

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

        Room room = player.getRoomUser().getRoom();
        InfobusManager bus = InfobusManager.getInstance();




        if(args[0].equalsIgnoreCase("status")) {
            player.send(new ALERT(bus.constructStatus()));
        }

        if(args[0].equalsIgnoreCase("start")) {
            bus.startPoll();
        }

        if(args[0].equalsIgnoreCase("reset")) {
            bus.reset();
        }

        if(args[0].equalsIgnoreCase("question")) {
            String question = StringUtil.filterInput(String.join(" ", IntStream.range(1, args.length).mapToObj(i -> args[i]).toArray(String[]::new)), true);
            bus.setQuestion(question);
        }

        if(args[0].equalsIgnoreCase("option") && args[1].equalsIgnoreCase("add") ) {
            if(args[2] == null) {
                player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "You're missing option"));
            }

            String option = StringUtil.filterInput(String.join(" ", IntStream.range(2, args.length).mapToObj(i -> args[i]).toArray(String[]::new)), true);
            bus.addOption(option);
        }

        if(args[0].equalsIgnoreCase("option") && args[1].equalsIgnoreCase("remove") ) {
            if(!NumberUtils.isCreatable(args[2])) {
                player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "To remove a question you need to the number found in :infobus status."));
                return;
            } else {
                 bus.removeOption(Integer.parseInt(args[2]));
            }
        }



        if(args[0].equalsIgnoreCase("close")) {
            InfobusManager.getInstance().closeDoor(room);
        }

        if(args[0].equalsIgnoreCase("open")) {
            InfobusManager.getInstance().openDoor(room);
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