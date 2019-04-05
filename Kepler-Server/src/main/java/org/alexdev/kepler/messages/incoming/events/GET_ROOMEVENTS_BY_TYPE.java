package org.alexdev.kepler.messages.incoming.events;

import org.alexdev.kepler.game.events.Event;
import org.alexdev.kepler.game.events.EventsManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.events.ROOMEVENT_LIST;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

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
