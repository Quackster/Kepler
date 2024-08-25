package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuse;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.infobus.InfobusManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import org.alexdev.kepler.messages.outgoing.alert.ALERT;
import org.alexdev.kepler.util.StringUtil;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.stream.IntStream;

public class InfobusCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuse.INFOBUS);
    }

    @Override
    public void addArguments() {
        this.arguments.add("subcommand");
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

        if(args[0].equalsIgnoreCase("help")) {

            StringBuilder help = new StringBuilder();
            help.append("INFOBUS COMMANDS:<br>").append("<br>");
            help.append(":infobus open").append("<br>");
            help.append(":infobus close").append("<br>");
            help.append(":infobus question [text]").append("<br>");
            help.append(":infobus option add [text]").append("<br>");
            help.append(":infobus option remove [number]").append("<br>");
            help.append(":infobus status").append("<br>");
            help.append(":infobus reset");

            player.send(new ALERT(help.toString()));
        }

        if(args[0].equalsIgnoreCase("status")) {
            player.send(new ALERT(bus.constructStatus()));
        }

        if(args[0].equalsIgnoreCase("start")) {
            if(bus.getQuestion() != null && !bus.getQuestion().isEmpty()) {
                //System.out.println("Question is not empty");
                if(bus.getOptions().size() > 0) {
                    //System.out.println("Starting");
                    bus.startPoll();
                } else {
                    player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "You need to add some options for the question: :infobus option [add/remove] [option]"));
                }
            } else {
                player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "You need to set a question: :infobus question [question]"));
            }
        }

        if(args[0].equalsIgnoreCase("reset")) {
            player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Reset question, options and votes."));
            bus.reset();
        }

        if(args[0].equalsIgnoreCase("question")) {
            String question = StringUtil.filterInput(String.join(" ", IntStream.range(1, args.length).mapToObj(i -> args[i]).toArray(String[]::new)), true);
            player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Infobus question is now: " + question));
            bus.setQuestion(question);
        }

        if(args[0].equalsIgnoreCase("option") && args[1].equalsIgnoreCase("add")) {

            if(args[2] == null) {
                player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "You're missing option text"));
            }

            String option = StringUtil.filterInput(String.join(" ", IntStream.range(2, args.length).mapToObj(i -> args[i]).toArray(String[]::new)), true);
            bus.addOption(option);
            player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Added option to the question, see the status by executing :infobus status"));

        } else if(args[0].equalsIgnoreCase("option") && args[1].equalsIgnoreCase("remove")) {

            if(!NumberUtils.isCreatable(args[2])) {
                player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "To remove a question you need to the number found in :infobus status."));
            } else {
                // minus one, so it makes sense when using status (1 based)
                int option = Integer.parseInt(args[2])-1;
                bus.removeOption(option);
                player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Removed option from the question, see the status by executing :infobus status"));
            }

        } else if(args[0].equalsIgnoreCase("option") && args[1].isEmpty()) {
            player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Usage: :infobus option [add/remove] [option]"));
        }

        if(args[0].equalsIgnoreCase("close")) {
            player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Info-bus door closed."));
            InfobusManager.getInstance().closeDoor(room);
        }

        if(args[0].equalsIgnoreCase("open")) {
            player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Info-bus door opened."));
            InfobusManager.getInstance().openDoor(room);
        }
    }

    @Override
    public String getDescription() {
        return "To show infobus commands use [:infobus help]";
    }
}