package org.alexdev.kepler.messages.incoming.moderation;

import org.alexdev.kepler.dao.mysql.ModerationDao;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.moderation.ModerationActionType;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.texts.TextsManager;
import org.alexdev.kepler.messages.outgoing.rooms.user.HOTEL_VIEW;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.messages.outgoing.user.MODERATOR_ALERT;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.List;

public class MODERATORACTION implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        int commandCat = reader.readInt();
        int commandId = reader.readInt();

        String alertMessage = reader.readString();
        String notes = reader.readString();

        // TODO: refactor this if-else mess in something more syntactically pleasing
        if (commandCat == 0) {
            // User Command
            if (commandId == 0 && player.hasFuse(Fuseright.ROOM_ALERT)) {
                String alertUser = reader.readString();

                Player target = PlayerManager.getInstance().getPlayerByName(alertUser);

                if (target != null) {
                    target.send(new MODERATOR_ALERT(alertMessage));
                    ModerationDao.addLog(ModerationActionType.ALERT_USER, player.getDetails().getId(), target.getDetails().getId(), alertMessage, notes);
                } else {
                    player.send(new ALERT("Target user is not online."));
                }
            } else if (commandId == 1 && player.hasFuse(Fuseright.KICK)) {
                // Kick
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
            } else if (commandId == 2 && player.hasFuse(Fuseright.BAN)) {
                //Ban
                // TODO: Banning
            }
        } else if (commandCat == 1) {
            // Room Command
            if (commandId == 0 && player.hasFuse(Fuseright.ROOM_ALERT)) {
                List<Player> players = player.getRoomUser().getRoom().getEntityManager().getPlayers();

                for (Player target : players) {
                    target.send(new MODERATOR_ALERT(alertMessage));
                }

                ModerationDao.addLog(ModerationActionType.ROOM_ALERT, player.getDetails().getId(), -1, alertMessage, notes);
            } else if (commandId == 1 && player.hasFuse(Fuseright.ROOM_KICK)) {
                // Room Kick
                List<Player> players = player.getRoomUser().getRoom().getEntityManager().getPlayers();

                for (Player target : players) {
                    // Don't kick other moderators
                    if (target.hasFuse(Fuseright.ROOM_KICK)) {
                        continue;
                    }

                    target.getRoomUser().kick(false);

                    target.send(new HOTEL_VIEW());
                    target.send(new MODERATOR_ALERT(alertMessage));

                    ModerationDao.addLog(ModerationActionType.ROOM_KICK, player.getDetails().getId(), -1, alertMessage, notes);
                }
            }
        }
    }
}
