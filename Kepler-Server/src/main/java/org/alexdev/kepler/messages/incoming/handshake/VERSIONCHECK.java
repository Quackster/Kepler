package org.alexdev.kepler.messages.incoming.handshake;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.alert.OUDATEDVERSION;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.config.GameConfiguration;

public class VERSIONCHECK implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        
        Integer version = reader.readInt();
        Integer serverVersion = Integer.parseInt(GameConfiguration.getInstance().getString("client.version"));
        if(serverVersion > version) {
            player.send(new OUDATEDVERSION(serverVersion-version >= 2 ? true : false));
        }
    }
}
