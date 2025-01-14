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

public class BotsStatusCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuse.DEBUG);
    }

    @Override
    public void addArguments() {
        this.arguments.add("name");
        this.arguments.add("status");
        this.arguments.add("id");
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player) entity;

        boolean botsAreEnabled = player.getRoomUser().getRoom().getEntityManager().getEntitiesByClass(Bot.class).size() > 0;
        if (!botsAreEnabled) {
            player.send(new ALERT("Bots are disabled!"));
            return;
        }

        String name = args[0];
        String status = args[1];
        String id = args[2];

        boolean allBots = false;
        if(name.equals("all")) {
            allBots = true;
        }

        StatusType statusType;

        try {
            statusType = StatusType.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.send(new ALERT("Invalid status"));
            return;
        }



        try {
            Integer.parseInt(id);
        } catch (NumberFormatException e) {
            player.send(new ALERT("Invalid ID, must be a number"));
            return;
        }

        if(allBots) {
            BotManager.getInstance().setBotStatus(player.getRoomUser().getRoom(), statusType, id);
        } else {
            BotManager.getInstance().setBotStatus(name, player.getRoomUser().getRoom(), statusType, id);
        }
    }

    @Override
    public String getDescription() {
        return "In-game housekeeping for bots";
    }
}
