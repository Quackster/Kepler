package net.h4bbo.kepler.messages.incoming.events;

import net.h4bbo.kepler.game.events.Event;
import net.h4bbo.kepler.game.events.EventsManager;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.events.ROOMEVENT_LIST;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.util.List;

public class GET_ROOMEVENTS_BY_TYPE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        int categoryId = reader.readInt();

        if (categoryId < 0 || categoryId > 11) {
            return;
        }

        List<Event> eventList = EventsManager.getInstance().getEvents(categoryId);
        player.send(new ROOMEVENT_LIST(categoryId, eventList));
    }
}
