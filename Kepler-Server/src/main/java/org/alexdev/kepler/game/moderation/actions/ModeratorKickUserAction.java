package org.alexdev.kepler.game.moderation.actions;

import org.alexdev.kepler.dao.mysql.ModerationDao;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.moderation.ModerationAction;
import org.alexdev.kepler.game.moderation.ModerationActionType;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.texts.TextsManager;
import org.alexdev.kepler.messages.outgoing.rooms.user.HOTEL_VIEW;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.messages.outgoing.user.MODERATOR_ALERT;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class ModeratorKickUserAction implements ModerationAction {
    @Override
    public void performAction(Player player, Room room, String alertMessage, String notes, NettyRequest reader) {
        if (!player.hasFuse(Fuseright.KICK)) {
            return;
        }

        String alertUser = reader.readString();
        Player target = PlayerManager.getInstance().getPlayerByName(alertUser);

        if (target != null) {
            if (target.getDetails().getId() == player.getDetails().getId()) {
                return; // Can't kick yourself!
            }

            if (target.hasFuse(Fuseright.KICK)) {
                player.send(new ALERT(TextsManager.getInstance().getValue("modtool_rankerror")));
                return;
            }

            target.getRoomUser().kick(false);
            target.send(new HOTEL_VIEW());
            target.send(new MODERATOR_ALERT(alertMessage));

            ModerationDao.addLog(ModerationActionType.KICK_USER, player.getDetails().getId(), target.getDetails().getId(), alertMessage, notes);
        } else {
            player.send(new ALERT("Target user is not online."));
        }
    }
}
