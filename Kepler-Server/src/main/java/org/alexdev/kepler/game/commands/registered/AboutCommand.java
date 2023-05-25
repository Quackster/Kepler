package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.Kepler;
import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuse;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.alert.ALERT;

public class AboutCommand extends Command {

    @Override
    public void addPermissions() {
        this.permissions.add(Fuse.DEFAULT);
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player)entity;

        player.send(new ALERT("Project Odin, fork of Kepler - Habbo Hotel v14'ish emulation" +
                "<br>" +
                "<br>Current revision: " + Kepler.SERVER_VERSION +
                "<br>" +
                "<br>Contributors:" +
                "<br> - ThuGie, Webbanditten, Ascii, Sefhriloff, Copyright, Raptosaur, Hoshiko " +
                "<br>   Romuald, Glaceon, Nillus, Holo Team, Meth0d, office.boy, killerloader" +
                "<br>" +
                "<br>" +
                "Made by Quackster from RaGEZONE" + "<br>"
                + "<br>" +
                "https://github.com/Quackster/Kepler" + "<br>" +
                "https://github.com/Webbanditten/Odin"));
    }

    @Override
    public String getDescription() {
        return " Information about the software powering this retro";
    }
}
