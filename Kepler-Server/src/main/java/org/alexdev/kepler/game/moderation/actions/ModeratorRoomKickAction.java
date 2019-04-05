package org.alexdev.kepler.game.moderation.actions;

import org.alexdev.kepler.dao.mysql.ModerationDao;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.moderation.ModerationAction;
import org.alexdev.kepler.game.moderation.ModerationActionType;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.user.HOTEL_VIEW;
import org.alexdev.kepler.messages.outgoing.user.MODERATOR_ALERT;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.List;

public class ModeratorRoomKickAction implements ModerationAction {
    @Override
    public void performAction(Player player, Room room, String alertMessage, String notes, NettyRequest reader) {
        List<Player> players = player.getRoomUser().getRoom().getEntityManager().getPlayers();

        for (Player target : players) {
            // Don't kick other moderators
            if (target.hasFuse(Fuseright.ROOM_KICK)) {
                continue;
            }

            target.getRoomUser().kick(false);

            target.send(new HOTEL_VIEW());
            target.send(new MODERATOR_ALERT(alertMessage));
        }


        ModerationDao.addLog(ModerationActionType.ROOM_KICK, player.getDetails().getId(), -1, alertMessage, notes);
    }
}
