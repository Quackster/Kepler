package net.h4bbo.kepler.game.commands.registered;

import net.h4bbo.kepler.dao.mysql.ItemDao;
import net.h4bbo.kepler.game.commands.Command;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.item.base.ItemBehaviour;
import net.h4bbo.kepler.game.player.Player;

import java.util.ArrayList;
import java.util.List;

public class PickAllCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.DEFAULT);
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        if (!player.getRoomUser().getRoom().isOwner(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            return;
        }

        List<Item> itemsToPickup = new ArrayList<>();

        for (Item item : player.getRoomUser().getRoom().getItems()) {
            if (item.hasBehaviour(ItemBehaviour.PUBLIC_SPACE_OBJECT)) {
                continue; // The client does not allow picking up public room furniture, thus neither will the server
            }

            if (item.hasBehaviour(ItemBehaviour.POST_IT)) {
                continue; // The client does not allow picking up post-it's, thus neither will the server
            }

            itemsToPickup.add(item);
        }

        for (Item item : itemsToPickup) {
            item.setOwnerId(player.getDetails().getId());

            player.getRoomUser().getRoom().getMapping().pickupItem(player, item);
            player.getInventory().addItem(item);
        }

        ItemDao.updateItems(itemsToPickup);
        player.getInventory().getView("new");
    }

    @Override
    public String getDescription() {
        return "Allows the owner to pick up all furniture in a room";
    }
}
