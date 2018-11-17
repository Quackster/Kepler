package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import org.alexdev.kepler.messages.outgoing.rooms.user.FIGURE_CHANGE;
import org.alexdev.kepler.util.StringUtil;

import java.util.Arrays;

public class ChangeMottoCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.DEFAULT);
    }

    @Override
    public void addArguments() { }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        // Filter out possible packet injection attacks
        String motto;

        if (args.length > 0) {
            motto = StringUtil.filterInput(String.join(" ", args), true);
        } else {
            motto = "";
        }

        // Update motto
        player.getDetails().setMotto(motto);
        PlayerDao.saveMotto(player.getDetails());

        // Notify room of changed motto
        player.getRoomUser().getRoom().send(new FIGURE_CHANGE(player.getRoomUser().getInstanceId(), player.getDetails()));
    }

    @Override
    public String getDescription() {
        return "Change your motto";
    }
}
