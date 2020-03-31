package org.alexdev.kepler.messages.incoming.purse;

import org.alexdev.kepler.dao.mysql.PurseDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.purse.CreditLog;
import org.alexdev.kepler.messages.outgoing.purse.CREDIT_LOG;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.List;

public class GETUSERCREDITLOG implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {

        List<CreditLog> creditLog = PurseDao.getCreditLog(player.getDetails().getId());

        player.send(new CREDIT_LOG(player.getDetails(), creditLog));
    }
}
