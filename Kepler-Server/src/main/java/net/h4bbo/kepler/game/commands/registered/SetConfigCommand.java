package net.h4bbo.kepler.game.commands.registered;

import net.h4bbo.kepler.dao.mysql.SettingsDao;
import net.h4bbo.kepler.game.commands.Command;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.alert.ALERT;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.kepler.util.config.writer.GameConfigWriter;

public class SetConfigCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.ADMINISTRATOR_ACCESS);
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
