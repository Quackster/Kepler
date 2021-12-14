package org.alexdev.kepler.messages.outgoing.catalogue;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class DELIVER_PRESENT extends MessageComposer {
    private String sprite;
    private String customData;
    private String colour;

    public DELIVER_PRESENT(String sprite, String getCustomData, String colour) {
        this.sprite = sprite;
        this.customData = getCustomData;
        this.colour = colour;
    }


    @Override
    public void compose(NettyResponse response) {
        response.writeDelimeter(this.sprite, (char) 13);

        if (this.sprite.equalsIgnoreCase("poster")) {
            response.writeDelimeter(this.sprite + " " + this.customData, (char) 13);
        } else {
            response.writeDelimeter(this.sprite, (char) 13);
        }

        response.write(this.colour);
    }

    @Override
    public short getHeader() {
        return 129; // "BA"
    }
}
