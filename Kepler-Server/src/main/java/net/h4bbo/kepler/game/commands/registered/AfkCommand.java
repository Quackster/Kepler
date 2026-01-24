package net.h4bbo.kepler.game.commands.registered;

import net.h4bbo.kepler.game.commands.Command;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.enums.StatusType;
import net.h4bbo.kepler.messages.outgoing.rooms.user.USER_STATUSES;

import java.util.List;

public class AfkCommand extends Command {
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

        if (player.getRoomUser().isWalking()) {
            return;
        }

        if (!player.getRoomUser().containsStatus(StatusType.AVATAR_SLEEP)) {
            player.getRoomUser().removeDrinks();
            player.getRoomUser().setStatus(StatusType.AVATAR_SLEEP, "");
            player.getRoomUser().setNeedsUpdate(true);

            // Send immediate update to client
            player.send(new USER_STATUSES(List.of(player)));
        }
    }

    @Override
    public String getDescription() {
        return "Put your eyes to sleep";
    }
}
