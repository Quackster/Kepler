package net.h4bbo.kepler.messages.incoming.rooms.moderation;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.outgoing.rooms.FLATNOTALLOWEDTOENTER;
import net.h4bbo.kepler.messages.outgoing.rooms.FLAT_LETIN;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class LETUSERIN implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (!room.hasRights(player.getDetails().getId())) {
            return;
        }

        String username = reader.readString();
        String successToken = reader.contents();
        boolean canEnter = successToken.equals("A");

        Player enteringPlayer = PlayerManager.getInstance().getPlayerByName(username);

        if (enteringPlayer == null) {
            return;
        }

        if (canEnter) {
            enteringPlayer.getRoomUser().setAuthenticateId(room.getId());
            enteringPlayer.send(new FLAT_LETIN());
        } else {
            enteringPlayer.send(new FLATNOTALLOWEDTOENTER());
        }
    }
}
