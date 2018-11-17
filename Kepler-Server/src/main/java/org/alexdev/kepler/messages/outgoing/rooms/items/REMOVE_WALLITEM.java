package org.alexdev.kepler.messages.outgoing.rooms.items;

import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class REMOVE_WALLITEM extends MessageComposer {
    private final Item item;

    public REMOVE_WALLITEM(Item item) {
        this.item = item;
    }

    @Override
    public void compose(NettyResponse response) {
        response.write(this.item.getId());
    }

    @Override
    public short getHeader() {
        return 84; // "AT"
    }
}
