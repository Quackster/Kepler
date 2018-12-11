package org.alexdev.kepler.messages.outgoing.rooms.items;

import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class BROADCAST_TELEPORTER extends MessageComposer {
    private final Item item;
    private final String name;
    private final boolean disappearUser;

    public BROADCAST_TELEPORTER(Item item, String name, boolean disappearUser) {
        this.item = item;
        this.name = name;
        this.disappearUser = disappearUser;
    }

    @Override
    public void compose(NettyResponse response) {
        response.write(this.item.getId());
        response.write("/");
        response.write(this.name);
        response.write("/");
        response.write(this.item.getDefinition().getSprite());
    }

    @Override
    public short getHeader() {
        if (this.disappearUser) {
            return 89; // "AY"
        } else {
            return 92; // "A\"
        }
    }
}
