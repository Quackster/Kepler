package org.alexdev.kepler.game.moderation.actions;

import org.alexdev.kepler.dao.mysql.ModerationDao;
import org.alexdev.kepler.game.fuserights.Fuse;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.moderation.AuditLogType;
import org.alexdev.kepler.game.moderation.ModerationAction;
import org.alexdev.kepler.game.moderation.ModerationActionType;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.texts.TextsManager;
import org.alexdev.kepler.messages.outgoing.rooms.user.HOTEL_VIEW;
import org.alexdev.kepler.messages.outgoing.alert.ALERT;
import org.alexdev.kepler.messages.outgoing.user.MODERATOR_ALERT;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class ModeratorKickUserAction implements ModerationAction {
    @Override
    public void performAction(Player player, Room room, String alertMessage, String notes, NettyRequest reader) {
        String alertUser = reader.readString();
        doAction(alertUser, player, room, alertMessage, notes, player.getDetails().getId());
    }

    public void doAction(String alertUser, Player player, Room room, String alertMessage, String notes, int adminUserId) {
        if (player != null && !player.hasFuse(Fuse.BAN)) {
            return;
        }

        Player target = PlayerManager.getInstance().getPlayerByName(alertUser);

        if (target != null) {
            if (player != null && target.getDetails().getId() == player.getDetails().getId()) {
                return; // Can't kick yourself!
            }

            if (target.hasFuse(Fuse.KICK)) {
                if(player != null) player.send(new ALERT(TextsManager.getInstance().getValue("modtool_rankerror")));
                return;
            }

            target.getRoomUser().kick(false);
            target.send(new HOTEL_VIEW());
            target.send(new MODERATOR_ALERT(alertMessage));

            ModerationDao.addLog(AuditLogType.KICK_USER, adminUserId, target.getDetails().getId(), alertMessage, notes, 0);
        } else {
            if(player != null) player.send(new ALERT("Target user is not online."));
        }
    }
}
