package org.alexdev.kepler.game.item.interactors.types;


import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.triggers.GenericTrigger;

public class DefaultInteractor extends GenericTrigger {
    public void onEntityStop(Player player, Room room, Item item, int status) {
        if (item.hasBehaviour(ItemBehaviour.ELEVATION)) {
            System.out.println("Entity stop for elevation");
            item.getDefinition().setTopHeight(item.getElevation());
            item.getTile().setTileHeight(item.getElevation());
        }
    }
}
