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
        
        player.send(new ALERT("Project Kepler - Habbo Hotel v21 emulation" +
                "<br>" +
                "<br>Contributors:" +
                "<br> - Hoshiko, ThuGie, Alito, Ascii, Lightbulb, Raptosaur " + // Call for help
                "<br>   Romuald, Glaceon, Nillus, Holo Team, Meth0d, office.boy" + // Parts of Blunk
                "<br>" +
                "<br>" +
                "Made by Quackster from RaGEZONE"));
    }

    @Override
    public String getDescription() {
        return " Information about the software powering this retro";
    }
}
