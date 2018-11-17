package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.dao.mysql.CatalogueDao;
import org.alexdev.kepler.game.catalogue.CatalogueManager;
import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.apache.commons.lang3.StringUtils;

public class SetItemPriceCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.ADMINISTRATOR_ACCESS);
    }

    @Override
    public void addArguments() {
        this.arguments.add("sale code");
        this.arguments.add("price");
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;
        String saleCode = args[0];

        if (CatalogueManager.getInstance().getCatalogueItem(saleCode) == null) {
            player.send(new ALERT("That sale code doesn't exist!")); // TODO: Add locale
            return;
        }


        if (!StringUtils.isNumeric(args[1])) {
            player.send(new ALERT("You did not enter a number!")); // TODO: Add locale
            return;
        }

        int newPrice = Integer.parseInt(args[1]);
        var item = CatalogueManager.getInstance().getCatalogueItem(saleCode);

        if (item.getPrice() == newPrice) {
            player.send(new ALERT("You entered the same price that the catalogue item costs!")); // TODO: Add locale
            return;
        }

        CatalogueDao.setPrice(item.getSaleCode(), newPrice);

        String word = "increased";

        if (item.getPrice() > newPrice) {
            word = "decreased";
        }

        // TODO: Add locale
        player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "The " + item.getName() + " has successfully " + word + " from " + item.getPrice() + " to " + newPrice));
        item.setPrice(newPrice);
    }

    @Override
    public String getDescription() {
        return "In-game housekeeping for the catalogue item prices.";
    }
}
