package org.alexdev.kepler.game.commandqueue;

import com.google.gson.Gson;
import org.alexdev.kepler.dao.mysql.*;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.messenger.Messenger;
import org.alexdev.kepler.game.messenger.MessengerMessage;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.incoming.messenger.MESSENGER_GETREQUESTS;
import org.alexdev.kepler.messages.incoming.user.GET_INFO;
import org.alexdev.kepler.messages.outgoing.messenger.CAMPAIGN_MSG;
import org.alexdev.kepler.messages.outgoing.messenger.MESSENGER_INIT;
import org.alexdev.kepler.messages.outgoing.messenger.ROOMFORWARD;
import org.alexdev.kepler.messages.outgoing.rooms.user.HOTEL_VIEW;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;

import java.util.List;
import java.util.Map;
class CommandTemplate {
    public int UserId;
    public int Credits;
    public int DefinitionId;
    public int RoomId;
    public String RoomType;
    public String Message;
    public String MessageUrl;
    public String MessageLink;
    public int FriendRequestTo;
    public boolean OnlineOnly;
}
public class CommandQueueManager {
    private static CommandQueueManager instance;

    /**
     * Execute new commands
     */
    public void executeCommands() {
        List<CommandQueue> commandsToExecute = CommandQueueDao.getNotYetExecutedCommands();

        for (int i = 0; i < commandsToExecute.size(); i++) {
            CommandQueue cq = commandsToExecute.get(i);
            System.out.println(commandsToExecute.get(i).getCommand());
            this.handleCommand(cq);
        }

    }

    public void handleCommand(CommandQueue cq) {
        // Mark command as executed
        CommandQueueDao.setExecuted(cq);
        try {
            CommandTemplate commandArgs = new Gson().fromJson(cq.getArguments(), CommandTemplate.class);
            if (cq.getCommand().equalsIgnoreCase("refresh_appearance")) {
                refreshAppearanceCommand(commandArgs);
            } else if (cq.getCommand().equalsIgnoreCase("update_credits")) {
                updateCredits(commandArgs);
            } else if (cq.getCommand().equalsIgnoreCase("reduce_credits")) {
                reduceCredits(commandArgs);
            } else if (cq.getCommand().equalsIgnoreCase("purchase_furni")) {
                purchaseFurni(commandArgs);
            } else if (cq.getCommand().equalsIgnoreCase("roomForward")) {
                roomForward(commandArgs);
            } else if (cq.getCommand().equalsIgnoreCase("campaign")) {
                campaign(commandArgs);
            }
        } catch (Exception e) {
            Log.getErrorLogger().error("Failed to execute command, invalid parameters for " + cq.getCommand() + " using arguments = " + cq.getArguments() + " ERROR: " + e);
        }

    }

    private void roomForward(CommandTemplate commandArgs) {
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

    private void campaign(CommandTemplate commandArgs) {
        var onlinePlayers = PlayerManager.getInstance().getPlayers();
        if(commandArgs.OnlineOnly) {
            for (Player p : onlinePlayers) {
                MessengerDao.newCampaignMessage(p.getDetails().getId(), commandArgs.Message, commandArgs.MessageLink, commandArgs.MessageUrl);
            }
        } else {
            MessengerDao.newCampaignMessage(commandArgs.Message, commandArgs.MessageLink, commandArgs.MessageUrl);
        }

        for (Player p : onlinePlayers) {
            var messages = MessengerDao.getUnreadMessages(p.getDetails().getId());
            for (MessengerMessage m : messages.values()) {
                if(m.getFromId() == 0) {
                    System.out.println("Yep message");
                    p.send(new CAMPAIGN_MSG(m));
                }
            }
        }
    }


    public void purchaseFurni(CommandTemplate commandArgs) {
        Player player = PlayerManager.getInstance().getPlayerById(commandArgs.UserId);
        if(player == null) return;
        Item item = new Item();
        item.setOwnerId(player.getDetails().getId());
        item.setDefinitionId(commandArgs.DefinitionId);

        try {
            ItemDao.newItem(item);
        } catch(Exception e) {
            Log.getErrorLogger().error("Couldnt add furni");
        }
        player.getInventory().addItem(item);
    }

    public void updateCredits(CommandTemplate commandArgs) {
        Player player = PlayerManager.getInstance().getPlayerById(commandArgs.UserId);
        if(player == null) return;

        if (commandArgs.Credits > 0) {
            player.getDetails().setCredits(player.getDetails().getCredits() + commandArgs.Credits);
            player.send(new CREDIT_BALANCE(player.getDetails()));
        }

        player.send(new CREDIT_BALANCE(player.getDetails()));
    }

    public void reduceCredits(CommandTemplate commandArgs) {
        System.out.println("UserId:" + commandArgs.UserId + " - Amount : " + commandArgs.Credits);
        Player player = PlayerManager.getInstance().getPlayerById(commandArgs.UserId);

        if(player != null) {
            if (commandArgs.Credits > 0) {
                player.getDetails().setCredits(player.getDetails().getCredits() - commandArgs.Credits);
                player.send(new CREDIT_BALANCE(player.getDetails()));
            }
            player.send(new CREDIT_BALANCE(player.getDetails()));
        }
    }

    public void refreshAppearanceCommand(CommandTemplate commandArgs) {
        Player player = PlayerManager.getInstance().getPlayerById(commandArgs.UserId);
        if(player == null) return;

        player.getRoomUser().refreshAppearance();
    }

    /**
     * Get the {@link CommandQueueManager} instance
     *
     * @return the item manager instance
     */
    public static CommandQueueManager getInstance() {
        if (instance == null) {
            instance = new CommandQueueManager();
        }

        return instance;
    }
}
