package org.alexdev.kepler.game.commandqueue;

import com.google.gson.Gson;
import org.alexdev.kepler.game.commandqueue.commands.*;
import org.alexdev.kepler.log.Log;

public class CommandQueueManager {
    private static CommandQueueManager instance;

    public void handleCommand(CommandQueue cq) {
        try {
            CommandTemplate commandArgs = new Gson().fromJson(cq.getArguments(), CommandTemplate.class);
            Command command = null;
            CommandType commandType = CommandType.findCommandType(cq.getCommand().toLowerCase());
            if(commandType == null) {
                Log.getErrorLogger().error("Failed to execute command, invalid command name " + cq.getCommand() + " using arguments = " + cq.getArguments());
                return;
            }

            switch (commandType) {
                case REFRESH_APPEARANCE:
                    command = new RefreshAppearanceCommand();
                    break;
                case UPDATE_CREDITS:
                    command = new UpdateCreditsCommand();
                    break;
                case REDUCE_CREDITS:
                    command = new ReduceCreditsCommand();
                    break;
                case PURCHASE_FURNI:
                    command = new PurchaseFurniCommand();
                    break;
                case ROOM_FORWARD:
                    command = new RoomForwardCommand();
                    break;
                case CAMPAIGN:
                    command = new CampaignCommand();
                    break;
                case UPDATE_ROOM:
                    command = new RoomUpdateCommand();
                    break;
                case REMOTE_ALERT:
                    command = new RemoteAlertCommand();
                    break;
                case REMOTE_KICK:
                    command = new RemoteKickCommand();
                    break;
                case REMOTE_BAN:
                    command = new RemoteBanCommand();
                    break;
                case TEST_PACKET:
                    command = new TestPacket();
                    break;
                case UPDATE_INFOBUS:
                    command = new UpdateInfobusCommand();
                    break;
                case RESET_BOTS:
                    command = new ResetBotCommand();
                    break;
                case BOT_TALK:
                    command = new BotTalkCommand();
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
