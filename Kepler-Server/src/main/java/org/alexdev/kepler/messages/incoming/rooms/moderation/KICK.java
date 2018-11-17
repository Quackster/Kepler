package org.alexdev.kepler.messages.incoming.rooms.moderation;

import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.texts.TextsManager;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class KICK implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        String playerName = reader.contents();

        Player target = PlayerManager.getInstance().getPlayerByName(playerName);

        if (target == null || target.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (target.getDetails().getId() == player.getDetails().getId()) {
            return; // Can't kick yourself!
        }

        // Don't allow kicking room owners if you aren't a moderator
        if (room.isOwner(target.getDetails().getId()) && !player.hasFuse(Fuseright.KICK)) {
            return;
        }

        // Don't allow kicking if they have permissions to kick too
        if (target.hasFuse(Fuseright.KICK)) {
            player.send(new ALERT(TextsManager.getInstance().getValue("modtool_rankerror")));
            return;
        }

        // Don't allow kicking if you don't have room rights and don't have fuse rights
        if (!room.hasRights(player.getDetails().getId()) && !player.hasFuse(Fuseright.KICK)) {
            player.send(new ALERT(TextsManager.getInstance().getValue("modtool_rankerror")));
            return;
        }

        target.getRoomUser().kick(false);
    }
}
