package org.alexdev.kepler.messages.outgoing.rooms;

import org.alexdev.kepler.game.room.models.RoomModel;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;
import org.alexdev.kepler.util.StringUtil;

public class HEIGHTMAP extends MessageComposer {
    private final String heightmap;

    public HEIGHTMAP(String heightmap) {
        this.heightmap = heightmap;
    }

    public HEIGHTMAP(RoomModel roomModel) {
        this.heightmap = roomModel.getHeightmap();
    }

    @Override
    public void compose(NettyResponse response) {
        response.write(this.heightmap);
    }

    @Override
    public short getHeader() {
        return 31; // "@_"
    }
}
