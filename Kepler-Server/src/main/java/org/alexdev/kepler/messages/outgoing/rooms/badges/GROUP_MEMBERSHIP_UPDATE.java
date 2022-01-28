package org.alexdev.kepler.messages.outgoing.rooms.badges;

import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class GROUP_MEMBERSHIP_UPDATE extends MessageComposer {

    private PlayerDetails details;
    public GROUP_MEMBERSHIP_UPDATE(PlayerDetails details) {
        this.details = details;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(details.getId());
        response.writeInt(details.getGroup());
        response.writeInt(details.getGroupStatus());

    }

    @Override
    public short getHeader() {
        return 310; // "Dv"
    }
}
