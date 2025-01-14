package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.game.bot.Bot;
import org.alexdev.kepler.game.bot.BotManager;
import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuse;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.messages.outgoing.alert.ALERT;

public class BotsReset extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuse.DEBUG);
    }

    @Override
    public void addArguments() {
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player) entity;

        BotManager.getInstance().resetBots(player.getRoomUser().getRoom());
    }

    @Override
    public String getDescription() {
        return "In-game housekeeping for bots";
    }
}
