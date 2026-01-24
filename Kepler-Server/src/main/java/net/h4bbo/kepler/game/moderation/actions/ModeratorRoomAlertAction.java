package net.h4bbo.kepler.game.moderation.actions;

import net.h4bbo.kepler.dao.mysql.ModerationDao;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.moderation.ModerationAction;
import net.h4bbo.kepler.game.moderation.ModerationActionType;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.outgoing.moderation.MODERATOR_ALERT;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.util.List;

public class ModeratorRoomAlertAction implements ModerationAction {
    @Override
    public void performAction(Player player, Room room, String alertMessage, String notes, NettyRequest reader) {
        if (!player.hasFuse(Fuseright.ROOM_ALERT)) {
            return;
        }

        List<Player> players = player.getRoomUser().getRoom().getEntityManager().getPlayers();

        for (Player target : players) {
            target.send(new MODERATOR_ALERT(alertMessage));
        }

        ModerationDao.addLog(ModerationActionType.ROOM_ALERT, player.getDetails().getId(), -1, alertMessage, notes);
    }
}
