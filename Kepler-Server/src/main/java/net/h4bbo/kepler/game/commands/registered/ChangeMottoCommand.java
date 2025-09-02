package net.h4bbo.kepler.game.commands.registered;

import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.commands.Command;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.rooms.user.FIGURE_CHANGE;
import net.h4bbo.kepler.util.StringUtil;

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
        PlayerDao.saveMotto(player.getDetails().getId(), motto);

        // Notify room of changed motto
        player.getRoomUser().getRoom().send(new FIGURE_CHANGE(player.getRoomUser().getInstanceId(), player.getDetails()));
    }

    @Override
    public String getDescription() {
        return "Change your motto";
    }
}
