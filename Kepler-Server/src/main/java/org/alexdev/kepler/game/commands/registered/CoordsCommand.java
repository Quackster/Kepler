package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuse;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.RoomUserStatus;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.util.StringUtil;

import java.util.Map;

public class CoordsCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuse.ADMINISTRATOR_ACCESS);
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

        String statuses = "";
        for (Map.Entry <String, RoomUserStatus> pair : player.getRoomUser().getStatuses().entrySet()) {
            statuses += String.format("%s - ", pair.getKey(), pair.getValue());
        }

        player.send(new ALERT("Your coordinates:<br>" +
                "X: " + player.getRoomUser().getPosition().getX() + "<br>" +
                "Y: " + player.getRoomUser().getPosition().getY() + "<br>" +
                "Z: " + Double.toString(StringUtil.format(player.getRoomUser().getPosition().getZ())) + "<br>" + "<br>" +
                "Head rotation: " + player.getRoomUser().getPosition().getHeadRotation() + "<br>" +
                "Body rotation: " + player.getRoomUser().getPosition().getBodyRotation() + "<br>" +
                "Statuses: " + statuses + "<br>" +
                "AFK Time: " + player.getRoomUser().getTimerManager().getAfkTimer() + "<br>" +
                "Sleep Time: " + player.getRoomUser().getTimerManager().getSleepTimer()));
    }

    @Override
    public String getDescription() {
        return "Shows the coordinates in the room";
    }
}
