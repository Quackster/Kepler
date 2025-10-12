package org.alexdev.kepler.messages.incoming.handshake;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.UUID;

public class UNIQUEID implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        String machineId = reader.readString();

        if (machineId == null) {
            player.kickFromServer();
            return;
        }

        //if (machineId.isBlank() || !(machineId.length() == 33 && machineId.startsWith("#"))) {
        //    player.getNetwork().setClientMachineId("#" + UUID.randomUUID().toString().toUpperCase().replace("-", ""));
        //    player.getNetwork().setSaveMachineId(true);
        //} else {
        //    player.getNetwork().setClientMachineId(machineId);
        //    player.send(new UniqueIDMessageEvent(machineId));
        //}
    }
}
