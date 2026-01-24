package net.h4bbo.kepler.game.commands.registered;

import net.h4bbo.kepler.game.commands.Command;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.messages.outgoing.alert.ALERT;
import net.h4bbo.kepler.util.StringUtil;

public class HotelAlertCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.ADMINISTRATOR_ACCESS);
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        // Concatenate all arguments
        String alert = StringUtil.filterInput(String.join(" ", args), true);

        // Send all players an alert
        PlayerManager.getInstance().sendAll(new ALERT(alert));
    }

    @Override
    public String getDescription() {
        return "Sends an alert hotel-wide";
    }
}