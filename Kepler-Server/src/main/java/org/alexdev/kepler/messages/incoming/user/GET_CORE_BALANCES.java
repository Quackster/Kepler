package org.alexdev.kepler.messages.incoming.user;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.currencies.CORE_CURRENCY_BALANCE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_CORE_BALANCES implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        player.send(new CORE_CURRENCY_BALANCE(player.getDetails()));
    }
}
