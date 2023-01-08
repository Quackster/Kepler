package org.alexdev.kepler.game.commandqueue.commands;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.commandqueue.CommandTemplate;
import org.alexdev.kepler.game.moderation.actions.ModeratorBanUserAction;
import org.alexdev.kepler.game.moderation.actions.ModeratorKickUserAction;
import org.alexdev.kepler.game.player.PlayerDetails;

public class RemoteKickCommand implements Command {
    public void executeCommand(CommandTemplate commandArgs) {
        for (String username : commandArgs.Users) {
            PlayerDetails details = PlayerDao.getDetails(username);
            if(details != null) {
                ModeratorKickUserAction kickAction = new ModeratorKickUserAction();
                String user = commandArgs.Users.get(commandArgs.Users.size()-1);
                if(user != null) {
                    PlayerDetails player = PlayerDao.getDetails(user.toLowerCase());
                    if(player == null) return;

                    kickAction.doAction(player.getName(), null, null, commandArgs.Message, commandArgs.ExtraInfo, commandArgs.UserId);
                }
            }
        }
    }
}
