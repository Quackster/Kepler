package org.alexdev.kepler.game.commandqueue;

import com.google.gson.Gson;
import org.alexdev.kepler.dao.mysql.*;
import org.alexdev.kepler.game.commandqueue.commands.*;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.messenger.MessengerMessage;
import org.alexdev.kepler.game.moderation.actions.ModeratorBanUserAction;
import org.alexdev.kepler.game.moderation.actions.ModeratorKickUserAction;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.messenger.CAMPAIGN_MSG;
import org.alexdev.kepler.messages.outgoing.messenger.ROOMFORWARD;
import org.alexdev.kepler.messages.outgoing.user.MODERATOR_ALERT;
import org.alexdev.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;

import java.util.List;

public class CommandQueueManager {
    private static CommandQueueManager instance;

    /**
     * Execute new commands
     */
    public void executeCommands() {
        List<CommandQueue> commandsToExecute = CommandQueueDao.getNotYetExecutedCommands();

        for (int i = 0; i < commandsToExecute.size(); i++) {
            CommandQueue cq = commandsToExecute.get(i);
            this.handleCommand(cq);
        }

    }

    public void handleCommand(CommandQueue cq) {
        // Mark command as executed
        CommandQueueDao.setExecuted(cq);
        try {
            CommandTemplate commandArgs = new Gson().fromJson(cq.getArguments(), CommandTemplate.class);
            Command command = null;
            switch (cq.getCommand().toLowerCase()) {
                case "refresh_appearance":
                    command = new RefreshAppearanceCommand();
                    break;
                case "update_credits":
                    command = new UpdateCreditsCommand();
                    break;
                case "reduce_credits":
                    command = new ReduceCreditsCommand();
                    break;
                case "purchase_furni":
                    command = new PurchaseFurniCommand();
                    break;
                case "roomForward":
                    command = new RoomForwardCommand();
                    break;
                case "campaign":
                    command = new CampaignCommand();
                    break;
                case "update_room":
                    command = new RoomUpdateCommand();
                    break;
                case "remote_alert":
                    command = new RemoteAlertCommand();
                    break;
                case "remote_kick":
                    command = new RemoteKickCommand();
                    break;
                case "remote_ban":
                    command = new RemoteBanCommand();
                    break;
                default:
                    break;
            }
            if(command != null) {
                command.executeCommand(commandArgs);
            }
        } catch (Exception e) {
            Log.getErrorLogger().error("Failed to execute command, invalid parameters for " + cq.getCommand() + " using arguments = " + cq.getArguments() + " ERROR: " + e);
        }

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
