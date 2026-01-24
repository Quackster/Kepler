package net.h4bbo.kepler.messages.incoming.messenger;

import net.h4bbo.kepler.game.messenger.Messenger;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.messages.outgoing.messenger.MESSENGER_INIT;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
import net.h4bbo.kepler.util.config.ServerConfiguration;

public class MESSENGERINIT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        Messenger messenger = PlayerManager.getInstance().getMessengerData(player.getDetails().getId());

        player.send(new MESSENGER_INIT(player, player.getDetails().getConsoleMotto(), messenger));

        // Get requests manually
        // if (player.getVersion() <= 14) {
            new MESSENGER_GETREQUESTS().handle(player, null);
        // }
    }
}
