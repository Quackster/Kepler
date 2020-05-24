package org.alexdev.kepler.game.commandqueue;

import org.alexdev.kepler.dao.mysql.*;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.incoming.user.GET_INFO;
import org.alexdev.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;

import java.util.List;
import java.util.Map;

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

        switch (cq.getCommand()) {
            case "refresh_appearance":
                refreshAppearanceCommand(Integer.parseInt(cq.getArguments()));
            case "update_credits":
                updateCredits(Integer.parseInt(cq.getArguments().split(",")[0]), Integer.parseInt(cq.getArguments().split(",")[1]));
            case "reduce_credits":
                reduceCredits(Integer.parseInt(cq.getArguments().split(",")[0]), Integer.parseInt(cq.getArguments().split(",")[1]));
            case "purchase_furni":
                purchaseFurni(Integer.parseInt(cq.getArguments().split(",")[0]), Integer.parseInt(cq.getArguments().split(",")[1]));
            default:
                return;
        }
    }


    public void purchaseFurni(int userId, int definitionId) {
        Player player = PlayerManager.getInstance().getPlayerById(userId);
        if(player == null) return;
        Item item = new Item();
        item.setOwnerId(player.getDetails().getId());
        item.setDefinitionId(definitionId);

        try {
            ItemDao.newItem(item);
        } catch(Exception e) {
            Log.getErrorLogger().error("Couldnt add furni");
        }
        player.getInventory().addItem(item);
    }

    public void updateCredits(int userId, int amount) {
        Player player = PlayerManager.getInstance().getPlayerById(userId);
        if(player == null) return;

        if (amount > 0) {
            player.getDetails().setCredits(player.getDetails().getCredits() + amount);
            player.send(new CREDIT_BALANCE(player.getDetails()));
        }

        player.send(new CREDIT_BALANCE(player.getDetails()));
    }

    public void reduceCredits(int userId, int amount) {
        Player player = PlayerManager.getInstance().getPlayerById(userId);
        if(player == null) return;

        if (amount > 0) {
            player.getDetails().setCredits(player.getDetails().getCredits() - amount);
            player.send(new CREDIT_BALANCE(player.getDetails()));
        }

        player.send(new CREDIT_BALANCE(player.getDetails()));
    }

    public void refreshAppearanceCommand(int userId) {
        Player player = PlayerManager.getInstance().getPlayerById(userId);
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
