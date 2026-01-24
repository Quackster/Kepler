package net.h4bbo.kepler.messages.incoming.recycler;

import net.h4bbo.kepler.dao.mysql.RecyclerDao;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.recycler.RecyclerManager;
import net.h4bbo.kepler.messages.outgoing.recycler.RECYCLER_STATUS;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class GET_FURNI_RECYCLER_STATUS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        player.send(new RECYCLER_STATUS(
                RecyclerManager.getInstance().isRecyclerEnabled(),
                RecyclerDao.getSession(player.getDetails().getId())));
    }
}
