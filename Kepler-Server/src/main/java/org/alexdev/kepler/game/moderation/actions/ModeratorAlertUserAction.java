package org.alexdev.kepler.game.moderation.actions;

import org.alexdev.kepler.dao.mysql.ModerationDao;
import org.alexdev.kepler.game.fuserights.Fuse;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.moderation.AuditLogType;
import org.alexdev.kepler.game.moderation.ModerationActionType;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.moderation.ModerationAction;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.alert.ALERT;
import org.alexdev.kepler.messages.outgoing.user.MODERATOR_ALERT;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class ModeratorAlertUserAction implements ModerationAction {
    @Override
    public void performAction(Player player, Room room, String alertMessage, String notes, NettyRequest reader) {
        if (!player.hasFuse(Fuse.ROOM_ALERT)) {
            return;
        }

        String alertUser = reader.readString();
        Player target = PlayerManager.getInstance().getPlayerByName(alertUser);

        if (target != null) {
            target.send(new MODERATOR_ALERT(alertMessage));
            ModerationDao.addLog(AuditLogType.ALERT_USER, player.getDetails().getId(), target.getDetails().getId(), alertMessage, notes, 0);
        } else {
            player.send(new ALERT("Target user is not online."));
        }
    }
}