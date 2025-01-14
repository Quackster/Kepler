package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.dao.mysql.SettingsDao;
import org.alexdev.kepler.game.bot.Bot;
import org.alexdev.kepler.game.bot.BotManager;
import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuse;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.alert.ALERT;
import org.alexdev.kepler.util.config.GameConfiguration;
import org.alexdev.kepler.util.config.writer.GameConfigWriter;

public class BotsCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuse.DEBUG);
    }

    @Override
    public void addArguments() {
        this.arguments.add("enabled");
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        String enabled = args[0];

        if (!enabled.equals("enable") && !enabled.equals("disable")) {
            player.send(new ALERT("Invalid value, must be true or false!")); // TODO: Add locale
            return;
        }

        boolean enableBots = enabled.equals("enable");

        if(enableBots) {
            boolean botsAreEnabled = player.getRoomUser().getRoom().getEntityManager().getEntitiesByClass(Bot.class).size() > 0;
            if (botsAreEnabled) {
                player.send(new ALERT("Bots are already enabled!"));
                return;
            }
            BotManager.getInstance().addBots(player.getRoomUser().getRoom());
        } else {
            BotManager.getInstance().removeBots(player.getRoomUser().getRoom());
        }



        player.send(new ALERT("Bots has been " + enabled + "d!"));
    }

    @Override
    public String getDescription() {
        return "In-game housekeeping for bots";
    }
}
