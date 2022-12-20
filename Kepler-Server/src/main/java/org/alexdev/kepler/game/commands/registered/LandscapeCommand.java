package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuse;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.rooms.FLATPROPERTY;

public class LandscapeCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuse.DEBUG);
    }

    @Override
    public void addArguments() {
        this.arguments.add("value");
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

        String value = args[0];
        player.send(new FLATPROPERTY("landscape",value));
    }

    @Override
    public String getDescription() {
        return "Sends back FLATPROPERTY('landscape', value)";
    }
}
