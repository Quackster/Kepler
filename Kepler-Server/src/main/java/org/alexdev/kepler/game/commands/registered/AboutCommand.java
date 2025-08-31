package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.alert.ALERT;

public class AboutCommand extends Command {

    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.DEFAULT);
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player)entity;

        player.send(new ALERT("Project Kepler - Habbo Hotel v26 emulation" +
                "<br>" +
                "<br>Max version supported: r26_20080915_0408_7984_61ccb5f8b8797a3aba62c1fa2ca80169" +
                "<br>" +
                "<br>Originally based off Kepler" +
                "<br>" +
                "<br>" +
                "Made by Quackster from RaGEZONE"));
    }

    @Override
    public String getDescription() {
        return " Information about the software powering this retro";
    }
}
