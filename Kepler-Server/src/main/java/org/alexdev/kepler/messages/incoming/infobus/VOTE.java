package org.alexdev.kepler.messages.incoming.infobus;

import org.alexdev.kepler.game.infobus.InfobusManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class VOTE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        String vote = reader.contents();;
        InfobusManager.getInstance().addVote(Integer.parseInt(vote));
    }
}
