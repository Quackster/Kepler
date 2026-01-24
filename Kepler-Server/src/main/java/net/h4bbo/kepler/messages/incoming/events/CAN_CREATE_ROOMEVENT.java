package net.h4bbo.kepler.messages.incoming.events;

import net.h4bbo.kepler.game.events.EventsManager;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.events.ROOMEVENT_PERMISSION;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class CAN_CREATE_ROOMEVENT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        boolean canCreateEvent = EventsManager.getInstance().canCreateEvent(player);
        player.send(new ROOMEVENT_PERMISSION(canCreateEvent));
    }
}
