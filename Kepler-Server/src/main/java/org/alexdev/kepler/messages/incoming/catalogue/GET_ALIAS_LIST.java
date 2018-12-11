package org.alexdev.kepler.messages.incoming.catalogue;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.catalogue.ALIAS_TOGGLE;
import org.alexdev.kepler.messages.outgoing.catalogue.SPRITE_LIST;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_ALIAS_LIST implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        player.send(new SPRITE_LIST());
        player.send(new ALIAS_TOGGLE());
    }
}
