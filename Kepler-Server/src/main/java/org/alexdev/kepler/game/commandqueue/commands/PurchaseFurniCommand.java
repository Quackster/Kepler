package org.alexdev.kepler.game.commandqueue.commands;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.dao.mysql.RoomDao;
import org.alexdev.kepler.game.commandqueue.CommandTemplate;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.log.Log;

public class PurchaseFurniCommand implements Command {
    public void executeCommand(CommandTemplate commandArgs) {
        Player player = PlayerManager.getInstance().getPlayerById(commandArgs.UserId);
        if(player == null) return;
        Item item = new Item();
        item.setOwnerId(player.getDetails().getId());
        item.setDefinitionId(commandArgs.DefinitionId);

        try {
            ItemDao.newItem(item);
        } catch(Exception e) {
            Log.getErrorLogger().error("Couldnt add furni");
        }
        player.getInventory().addItem(item);
    }
}
