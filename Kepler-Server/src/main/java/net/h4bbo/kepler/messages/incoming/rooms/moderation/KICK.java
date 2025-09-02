package net.h4bbo.kepler.messages.incoming.rooms.moderation;

import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.texts.TextsManager;
import net.h4bbo.kepler.messages.outgoing.alert.ALERT;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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
