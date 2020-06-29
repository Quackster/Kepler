package org.alexdev.kepler.messages.outgoing.rooms.badges;

import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class GROUP_BADGES extends MessageComposer {

    @Override
    public void compose(NettyResponse response) {
        //response.write("IXXAs14114");
        response.writeInt(1);
        response.writeInt(1);
        response.writeString("s14114");
        //response.writeDelimeter("IXXAs14114", (char) 2);

    }

    @Override
    public short getHeader() {
        return 309; // "Du"
    }
}
