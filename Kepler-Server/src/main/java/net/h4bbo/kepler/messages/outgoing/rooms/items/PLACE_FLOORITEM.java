package net.h4bbo.kepler.messages.outgoing.rooms.items;

import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class PLACE_FLOORITEM extends MessageComposer {
    private final Item item;

    public PLACE_FLOORITEM(Item item) {
        this.item = item;
    }

    @Override
    public void compose(NettyResponse response) {
        this.item.serialise(response);
    }

    @Override
    public short getHeader() {
        return 93; // "A]"
    }
}
