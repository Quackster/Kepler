package org.alexdev.kepler.messages.incoming.bulletin;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.bulletin.ARTICLES_PAGE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_PAGE_ARTICLES implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        final int pageId = reader.readInt();

        player.send(new ARTICLES_PAGE(pageId));
    }
}
