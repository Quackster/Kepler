package org.alexdev.kepler.messages.incoming.handshake;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.alert.OUDATEDVERSION;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.config.GameConfiguration;

public class MACHINEID implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {

        String machineId = reader.readString();

        if (!PlayerDao.getLatestMachineId(player.getDetails().getId()).equals(machineId)) {
            PlayerDao.logMachineId(player.getDetails().getId(), machineId);
        }
    }
}
