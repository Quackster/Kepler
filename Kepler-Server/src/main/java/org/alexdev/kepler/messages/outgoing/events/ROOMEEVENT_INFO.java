package org.alexdev.kepler.messages.outgoing.events;

import org.alexdev.kepler.game.events.Event;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class ROOMEEVENT_INFO extends MessageComposer {
    private final Event event;

    public ROOMEEVENT_INFO(Event event) {
        this.event = event;
    }

    @Override
    public void compose(NettyResponse response) {
        if (this.event == null) {
            response.writeString(-1);
        } else {
            response.writeString(this.event.getEventHoster().getId());
            response.writeString(this.event.getEventHoster().getName());
            response.writeString(this.event.getRoomId());
            response.writeInt(this.event.getCategoryId());
            response.writeString(this.event.getName());
            response.writeString(this.event.getDescription());
            response.writeString(this.event.getStartedDate());
        }
    }

    @Override
    public short getHeader() {
        return 370;
    }
}
