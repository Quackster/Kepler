package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.room.mapping.RoomTile;
import org.alexdev.kepler.util.StringUtil;

public class SitCommand extends Command {

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

        if (player.getRoomUser().containsStatus(StatusType.SIT)) {
            return;
        }

        if (player.getRoomUser().containsStatus(StatusType.SWIM)) {
            return;
        }

        double height = 0.5;

        if (player.getRoomUser().getRoom().isPublicRoom()) {
            if (player.getRoomUser().getRoom().getModel().getName().startsWith("pool_")) {
                height = 0.0;
            }
        }

        int rotation = player.getRoomUser().getPosition().getRotation() / 2 * 2;
        Item item = player.getRoomUser().getCurrentItem();

        if (item != null) {
            if (item.hasBehaviour(ItemBehaviour.CAN_SIT_ON_TOP) || item.hasBehaviour(ItemBehaviour.CAN_LAY_ON_TOP)) {
                return; // Don't process :sit command on furniture that the user is already on.
            }

            if (!item.hasBehaviour(ItemBehaviour.ROLLER)) {
                height += item.getDefinition().getTopHeight();
            }
        }

        player.getRoomUser().getPosition().setRotation(rotation);
        player.getRoomUser().setStatus(StatusType.SIT, StringUtil.format(height));
        player.getRoomUser().removeStatus(StatusType.DANCE);
        player.getRoomUser().setNeedsUpdate(true);
    }

    @Override
    public String getDescription() {
        return "Parks your arse on the floor";
    }
}
