package net.h4bbo.kepler.messages.incoming.user;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class GET_CREDITS implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        player.send(new CREDIT_BALANCE(player.getDetails().getCredits()));
    }
}
