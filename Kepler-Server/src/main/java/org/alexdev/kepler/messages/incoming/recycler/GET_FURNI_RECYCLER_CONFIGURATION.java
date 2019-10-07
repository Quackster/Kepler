package org.alexdev.kepler.messages.incoming.recycler;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.recycler.RecyclerManager;
import org.alexdev.kepler.messages.outgoing.recycler.RECYCLER_CONFIGURATION;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_FURNI_RECYCLER_CONFIGURATION implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        /*

                config.put("recycler.timeout.seconds", "300");
        config.put("recycler.session.length.seconds", "3660");
        config.put("recycler.item.quarantine.seconds", "2592000");

         */
        player.send(new RECYCLER_CONFIGURATION(
                RecyclerManager.getInstance().isRecyclerEnabled(),
                RecyclerManager.getInstance().getRecyclerRewards(),
                RecyclerManager.getInstance().getRecyclerTimeoutSeconds(),
                RecyclerManager.getInstance().getRecyclerItemQuarantineSeconds(),
                RecyclerManager.getInstance().getRecyclerSessionLengthSeconds()
        ));
    }
}
