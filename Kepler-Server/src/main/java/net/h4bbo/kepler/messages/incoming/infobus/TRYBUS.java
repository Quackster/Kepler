package net.h4bbo.kepler.messages.incoming.infobus;

import net.h4bbo.kepler.game.infobus.InfobusManager;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.infobus.CANNOT_ENTER_BUS;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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
