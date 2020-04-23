package org.alexdev.kepler.game.commandqueue;

import org.alexdev.kepler.dao.mysql.CommandQueueDao;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.dao.mysql.SettingsDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.incoming.user.GET_INFO;

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
            default:
                return;
        }
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
