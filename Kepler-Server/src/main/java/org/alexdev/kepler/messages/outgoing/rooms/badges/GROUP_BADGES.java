package org.alexdev.kepler.messages.outgoing.rooms.badges;

import org.alexdev.kepler.game.group.Group;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.ArrayList;
import java.util.List;

public class GROUP_BADGES extends MessageComposer {

    private List<Group> groups;
    public GROUP_BADGES(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(groups.size());
        for (Group group : groups) {
            response.writeInt(group.getId());
            response.writeString(group.getBadge());
        }

    }

    @Override
    public short getHeader() {
        return 309; // "Du"
    }
}
