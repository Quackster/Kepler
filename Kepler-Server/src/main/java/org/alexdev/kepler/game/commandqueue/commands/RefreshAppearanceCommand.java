package org.alexdev.kepler.game.commandqueue.commands;

import org.alexdev.kepler.game.commandqueue.CommandTemplate;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;

public class RefreshAppearanceCommand implements Command {
    public void executeCommand(CommandTemplate commandArgs) {
        Player player = PlayerManager.getInstance().getPlayerById(commandArgs.UserId);
        if(player == null) return;

        player.getRoomUser().refreshAppearance();
    }
}
