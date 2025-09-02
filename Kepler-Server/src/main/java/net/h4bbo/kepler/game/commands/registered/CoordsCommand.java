package net.h4bbo.kepler.game.commands.registered;

import net.h4bbo.kepler.game.commands.Command;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.alert.ALERT;
import net.h4bbo.kepler.util.StringUtil;

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
                "Z: " + Double.toString(StringUtil.format(player.getRoomUser().getPosition().getZ())) + "<br>" + "<br>" +
                "Head rotation: " + player.getRoomUser().getPosition().getHeadRotation() + "<br>" +
                "Body rotation: " + player.getRoomUser().getPosition().getBodyRotation()));
    }

    @Override
    public String getDescription() {
        return "Shows the coordinates in the room";
    }
}
