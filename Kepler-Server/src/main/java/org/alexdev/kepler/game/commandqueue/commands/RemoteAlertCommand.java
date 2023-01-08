package org.alexdev.kepler.game.commandqueue.commands;

import org.alexdev.kepler.game.commandqueue.CommandTemplate;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.user.MODERATOR_ALERT;

public class RemoteAlertCommand implements Command {
    public void executeCommand(CommandTemplate commandArgs) {
        var onlinePlayers = PlayerManager.getInstance().getPlayers();
        for (Player p : onlinePlayers) {
            if(commandArgs.Users.stream().anyMatch(x -> x.equalsIgnoreCase(p.getDetails().getName()))) {
                p.send(new MODERATOR_ALERT(commandArgs.Message));
            }
        }
    }
}
