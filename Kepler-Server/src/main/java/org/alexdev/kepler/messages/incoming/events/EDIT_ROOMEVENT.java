package org.alexdev.kepler.messages.incoming.events;

import org.alexdev.kepler.dao.mysql.EventsDao;
import org.alexdev.kepler.game.events.Event;
import org.alexdev.kepler.game.events.EventsManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.events.ROOMEEVENT_INFO;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.StringUtil;

public class EDIT_ROOMEVENT implements MessageEvent {
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

        int category = reader.readInt();

        if (category < 1 || category > 11) {
            return;
        }

        String name = StringUtil.filterInput(reader.readString(), true);
        String description = StringUtil.filterInput(reader.readString(), true);

        event.setCategoryId(category);
        event.setName(name);
        event.setDescription(description);

        room.send(new ROOMEEVENT_INFO(event));
        EventsDao.save(event);
    }
}
