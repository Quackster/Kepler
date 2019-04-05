package org.alexdev.kepler.messages.incoming.events;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.events.ROOMEVENT_TYPES;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.config.GameConfiguration;

public class GET_ROOMEVENT_TYPE_COUNT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        player.send(new ROOMEVENT_TYPES(GameConfiguration.getInstance().getInteger("events.category.count")));
    }
}
