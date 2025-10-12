package org.alexdev.kepler.messages.outgoing.infostand;

import org.alexdev.kepler.game.infostand.InfoStandShopEntry;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class INFO_STAND_SHOP extends MessageComposer {
    private final List<InfoStandShopEntry> shop;

    public INFO_STAND_SHOP(List<InfoStandShopEntry> shop) {
        this.shop = shop;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.shop.size());

        for (var entry : this.shop) {
            response.writeString(entry.getProductCode());
            response.writeString(entry.getProductId());
            response.writeInt(entry.getType().getId());
            response.writeInt(entry.getCurrency());
            response.writeInt(entry.getPrice());
        }
    }

    @Override
    public short getHeader() {
        return 147;
    }
}