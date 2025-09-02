package net.h4bbo.kepler.messages.outgoing.inventory;

import net.h4bbo.kepler.game.inventory.Inventory;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

import java.util.Map;

public class INVENTORY extends MessageComposer {
    private final Inventory inventory;
    private final Map<Integer, Item> casts;

    public INVENTORY(Inventory inventory, Map<Integer, Item> casts) {
        this.inventory = inventory;
        this.casts = casts;
    }

    @Override
    public void compose(NettyResponse response) {
        for (var kvp : this.casts.entrySet()) {
            this.inventory.serialise(response, kvp.getValue(), kvp.getKey());
        }

        response.write((char) 13);
        response.write(this.inventory.getItems().size());
    }

    @Override
    public short getHeader() {
        return 140; // "BL"
    }
}
