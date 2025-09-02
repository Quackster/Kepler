package net.h4bbo.kepler.game.commands.registered;

import net.h4bbo.kepler.Kepler;
import net.h4bbo.kepler.game.commands.Command;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.alert.ALERT;

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
