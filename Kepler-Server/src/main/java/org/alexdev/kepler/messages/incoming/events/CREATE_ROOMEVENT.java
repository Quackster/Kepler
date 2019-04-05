package org.alexdev.kepler.messages.incoming.events;

import org.alexdev.kepler.game.events.Event;
import org.alexdev.kepler.game.events.EventsManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.events.ROOMEEVENT_INFO;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.StringUtil;

public class CREATE_ROOMEVENT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (!EventsManager.getInstance().canCreateEvent(player)) {
            return;
        }

        int category = reader.readInt();

        if (category < 1 || category > 11) {
            return;
        }

        String name = StringUtil.filterInput(reader.readString(), true);
        String description = StringUtil.filterInput(reader.readString(), true);

        Event event = EventsManager.getInstance().createEvent(player, category, name, description);
        player.getRoomUser().getRoom().send(new ROOMEEVENT_INFO(event));
    }
}
