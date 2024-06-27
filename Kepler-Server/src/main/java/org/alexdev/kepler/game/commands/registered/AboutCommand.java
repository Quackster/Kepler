package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.Kepler;
import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.ALERT;

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

        player.send(new ALERT("Project Kepler - Habbo Hotel v14 emulation" +
                "<br>" +
                "<br>Current revision: " + Kepler.SERVER_VERSION +
                "<br>" +
                "<br>Contributors:" +
                "<br> - ThuGie, Webbanditten, Ascii, Sefhriloff, Copyright, Raptosaur, Hoshiko " + // Call for help
                "<br>   Romuald, Glaceon, Nillus, Holo Team, Meth0d, office.boy, killerloader" +
                "<br>   Alito, wackfx" +
                "<br>" +
                "<br>" +
                "Made by Quackster from RaGEZONE"));
    }

    @Override
    public String getDescription() {
        return " Information about the software powering this retro";
    }
}
