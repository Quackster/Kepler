package org.alexdev.kepler.game.commandqueue.commands;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.commandqueue.CommandTemplate;
import org.alexdev.kepler.game.moderation.actions.ModeratorBanUserAction;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.user.MODERATOR_ALERT;

public class RemoteBanCommand implements Command {
    public void executeCommand(CommandTemplate commandArgs) {
        for (String username : commandArgs.Users) {
            PlayerDetails details = PlayerDao.getDetails(username);
            if(details != null) {
                ModeratorBanUserAction banAction = new ModeratorBanUserAction();
                String user = commandArgs.Users.get(commandArgs.Users.size()-1);
                if(user != null) {
                    PlayerDetails player = PlayerDao.getDetails(user.toLowerCase());
                    if(player == null) return;

                    banAction.doAction(null, player.getName(), commandArgs.BanLength, commandArgs.BanMachine, commandArgs.BanIp, commandArgs.Message, commandArgs.ExtraInfo, commandArgs.UserId);
                }
            }
        }

    }
}
