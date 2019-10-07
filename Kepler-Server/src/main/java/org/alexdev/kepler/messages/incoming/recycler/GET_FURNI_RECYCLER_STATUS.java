package org.alexdev.kepler.messages.incoming.recycler;

import org.alexdev.kepler.dao.mysql.RecyclerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.recycler.RecyclerManager;
import org.alexdev.kepler.messages.outgoing.recycler.RECYCLER_STATUS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_FURNI_RECYCLER_STATUS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        player.send(new RECYCLER_STATUS(
                RecyclerManager.getInstance().isRecyclerEnabled(),
                RecyclerDao.getSession(player.getDetails().getId())));
    }
}
