package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.dao.mysql.SettingsDao;
import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuse;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.util.config.GameConfiguration;
import org.alexdev.kepler.util.config.writer.GameConfigWriter;

public class SetConfigCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuse.DEBUG);
    }

    @Override
    public void addArguments() {
        this.arguments.add("setting");
        this.arguments.add("value");
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        String setting = args[0];
        String value = args[1];

        if (!GameConfiguration.getInstance().getConfig().containsKey(setting)) {
            player.send(new ALERT("The setting \"" + setting + "\" doesn't exist!")); // TODO: Add locale
            return;
        }

        String oldValue = GameConfiguration.getInstance().getConfig().get(setting);

        SettingsDao.updateSetting(setting, value);
        GameConfiguration.reset(new GameConfigWriter());

        player.send(new ALERT("The setting \"" + setting + "\" value has been updated from \"" + oldValue + "\" to \"" + value + "\"")); // TODO: Add locale
    }

    @Override
    public String getDescription() {
        return "In-game housekeeping for the catalogue item prices.";
    }
}
