package org.alexdev.kepler.messages.incoming.events;

import org.alexdev.kepler.game.events.EventsManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.events.ROOMEVENT_PERMISSION;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class CAN_CREATE_ROOMEVENT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        boolean canCreateEvent = EventsManager.getInstance().canCreateEvent(player);
        player.send(new ROOMEVENT_PERMISSION(canCreateEvent));
    }
}
