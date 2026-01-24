package net.h4bbo.kepler.game.commands.registered;

import net.h4bbo.kepler.game.commands.Command;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.player.Player;

public class PacketTestCommand extends Command {
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

        String packet = String.join(" ", args);

        for (int i = 0; i < 14; i++) {
            packet = packet.replace("{" + i + "}", Character.toString((char)i));
        }

        // Add ending packet suffix
        packet += Character.toString((char)1);

        player.sendObject(packet);
    }

    @Override
    public String getDescription() {
        return "Tests a Habbo client-sided packet";
    }
}
