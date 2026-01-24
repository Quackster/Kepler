package net.h4bbo.kepler.messages.outgoing.rooms.items;

import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

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
