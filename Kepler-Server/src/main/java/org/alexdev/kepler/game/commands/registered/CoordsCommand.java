package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.util.StringUtil;

public class CoordsCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.ADMINISTRATOR_ACCESS);
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

        player.send(new ALERT("Your coordinates:<br>" +
                "X: " + player.getRoomUser().getPosition().getX() + "<br>" +
                "Y: " + player.getRoomUser().getPosition().getY() + "<br>" +
                "Z: " + Double.toString(StringUtil.format(player.getRoomUser().getPosition().getZ()))));
    }

    @Override
    public String getDescription() {
        return "Shows the coordinates in the room";
    }
}
