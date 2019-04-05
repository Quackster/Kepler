package org.alexdev.kepler.messages.incoming.events;

import org.alexdev.kepler.game.events.Event;
import org.alexdev.kepler.game.events.EventsManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.events.ROOMEEVENT_INFO;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class QUIT_ROOMEVENT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (!room.isOwner(player.getDetails().getId())) {
            return;
        }

        if (!EventsManager.getInstance().hasEvent(room.getId())) {
            return;
        }

        Event event = EventsManager.getInstance().getEventByRoomId(room.getId());

        if (event == null) {
            return;
        }

        EventsManager.getInstance().removeEvent(event);
        room.send(new ROOMEEVENT_INFO(null));
    }
}
