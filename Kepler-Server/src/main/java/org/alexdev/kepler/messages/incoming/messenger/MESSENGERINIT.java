package org.alexdev.kepler.messages.incoming.messenger;

import org.alexdev.kepler.game.messenger.Messenger;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.messenger.MESSENGER_INIT;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.config.ServerConfiguration;

public class MESSENGERINIT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        Messenger messenger = PlayerManager.getInstance().getMessengerData(player.getDetails().getId());

        player.send(new MESSENGER_INIT(messenger));

        // Get requests manually
        if (ServerConfiguration.getInteger("version") <= 14) {
            new MESSENGER_GETREQUESTS().handle(player, null);
        }
    }
}
