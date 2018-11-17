package org.alexdev.kepler.messages.outgoing.rooms.items;

import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class PLACE_WALLITEM extends MessageComposer {
    private final Item item;

    public PLACE_WALLITEM(Item item) {
        this.item = item;
    }

    @Override
    public void compose(NettyResponse response) {
        this.item.serialise(response);
    }

    @Override
    public short getHeader() {
        return 83; // "AS"
    }
}
