package org.alexdev.kepler.messages.outgoing.rooms.items;

import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class IDATA extends MessageComposer {
    private String colour;
    private String text;
    private Item item;

    public IDATA(Item item, String colour, String text) {
        this.item = item;
        this.colour = colour;
        this.text = text;
    }

    public IDATA(Item item) {
        this.item = item;
    }

    @Override
    public void compose(NettyResponse response) {
        if (this.item.hasBehaviour(ItemBehaviour.POST_IT)) {
            response.writeDelimeter(this.item.getId(), (char) 9);
            response.writeDelimeter(this.colour, ' ');
            response.write(this.text);
        } else {
            response.writeDelimeter(this.item.getId(), (char) 9);
            response.write(Integer.toString(item.getId()) + " x " + item.getCustomData());
        }
    }

    @Override
    public short getHeader() {
        return 48; // "@p"
    }
}

