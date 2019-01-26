package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.moderation.Fuseright;
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

        player.send(new ALERT("Project Kepler - Habbo Hotel v14+ emulation" +
                "<br>" +
                "<br>Max version supported: r21_20080417_0343_5110_5527e6590eba8f3fb66348bdf271b5a2" +
                "<br>" +
                "<br>Contributors:" +
                "<br> - ThuGie, Alito, Ascii, Sefhriloff, Copyright, Raptosaur, Hoshiko " + // Call for help
                "<br>   Romuald, Glaceon, Nillus, Holo Team, Meth0d, office.boy" +
                "<br>" +
                "<br>" +
                "Made by Quackster from RaGEZONE"));
    }

    @Override
    public String getDescription() {
        return " Information about the software powering this retro";
    }
}
