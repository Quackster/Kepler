package org.alexdev.kepler.game.commandqueue.commands;

import org.alexdev.kepler.game.commandqueue.CommandTemplate;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;

public class ReduceCreditsCommand implements Command {
    public void executeCommand(CommandTemplate commandArgs) {
        Player player = PlayerManager.getInstance().getPlayerById(commandArgs.UserId);

        if(player != null) {
            if (commandArgs.Credits > 0) {
                player.getDetails().setCredits(player.getDetails().getCredits() - commandArgs.Credits);
                player.send(new CREDIT_BALANCE(player.getDetails()));
            }
            player.send(new CREDIT_BALANCE(player.getDetails()));
        }
    }
}
