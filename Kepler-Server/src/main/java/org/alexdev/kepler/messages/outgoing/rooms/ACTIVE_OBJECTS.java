package org.alexdev.kepler.messages.outgoing.rooms;

import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class ACTIVE_OBJECTS extends MessageComposer {
    private final List<Item> items;

    public ACTIVE_OBJECTS(Room room) {
        this.items = room.getItemManager().getFloorItems();
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.items.size());

        for (Item item : this.items) {
            item.serialise(response);
        }
    }
    @Override
    public short getHeader() {
        return 32; // "@`"
    }
}
