package net.h4bbo.kepler.messages.incoming.recycler;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.recycler.RecyclerManager;
import net.h4bbo.kepler.messages.outgoing.recycler.RECYCLER_CONFIGURATION;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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
