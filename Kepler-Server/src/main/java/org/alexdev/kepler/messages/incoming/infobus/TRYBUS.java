package org.alexdev.kepler.messages.incoming.infobus;

import org.alexdev.kepler.game.infobus.InfobusManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.infobus.CANNOT_ENTER_BUS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class TRYBUS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        // Do not process public room items
        if (!player.getRoomUser().getRoom().isPublicRoom()) {
            return;
        }

        if (!InfobusManager.getInstance().isDoorOpen()) {
            player.send(new CANNOT_ENTER_BUS("The Infobus is closed, there is no event right now. Please check back later."));
            return;
        }

        player.getRoomUser().walkTo(
                InfobusManager.getInstance().getDoorX(),
                InfobusManager.getInstance().getDoorY()); // Walk to enter square
    }
}
