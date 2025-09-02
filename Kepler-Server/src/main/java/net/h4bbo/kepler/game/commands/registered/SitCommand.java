package net.h4bbo.kepler.game.commands.registered;

import net.h4bbo.kepler.game.commands.Command;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.item.base.ItemBehaviour;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.enums.StatusType;
import net.h4bbo.kepler.util.StringUtil;

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
