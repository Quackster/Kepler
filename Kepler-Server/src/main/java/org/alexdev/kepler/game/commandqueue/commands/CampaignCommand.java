package org.alexdev.kepler.game.commandqueue.commands;

import org.alexdev.kepler.dao.mysql.MessengerDao;
import org.alexdev.kepler.game.commandqueue.CommandTemplate;
import org.alexdev.kepler.game.messenger.MessengerMessage;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.outgoing.messenger.CAMPAIGN_MSG;
import org.alexdev.kepler.messages.outgoing.messenger.ROOMFORWARD;

public class CampaignCommand implements Command {
    public void executeCommand(CommandTemplate commandArgs) {
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
                    p.send(new CAMPAIGN_MSG(m));
                }
            }
        }
    }
}
