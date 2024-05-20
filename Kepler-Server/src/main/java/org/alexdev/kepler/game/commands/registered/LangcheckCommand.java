package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuse;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.RoomUserStatus;
import org.alexdev.kepler.messages.outgoing.alert.ALERT;
import org.alexdev.kepler.messages.outgoing.alert.EPS_NOTIFY;
import org.alexdev.kepler.util.StringUtil;

import java.util.Map;

public class LangcheckCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuse.DEBUG);
    }

    @Override
    public void addArguments() {
        this.arguments.add("text");
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

        String text = args[0];

        player.send(new EPS_NOTIFY(text));
    }

    @Override
    public String getDescription() {
        return "Langcheck / EPS Notify. Not sure what this does.";
    }
}
