package org.alexdev.kepler.messages.outgoing.user;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.tag.Tag;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class UPDATE_REQUEST extends MessageComposer {
    @Override
    public void compose(NettyResponse response) {

        response.writeInt(0);
        response.writeInt(1);
    }

    @Override
    public short getHeader() {
        return 275;
    }
}
