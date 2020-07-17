package org.alexdev.kepler.messages.outgoing.user;

import org.alexdev.kepler.dao.mysql.MessengerDao;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.messenger.MessengerMessage;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.tag.Tag;
import org.alexdev.kepler.messages.outgoing.messenger.CAMPAIGN_MSG;
import org.alexdev.kepler.messages.types.PlayerMessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class USER_TAGS extends PlayerMessageComposer {
    private final PlayerDetails details;

    public USER_TAGS(PlayerDetails details) {
        this.details = details;
    }

    @Override
    public void compose(NettyResponse response) {

        response.writeInt(this.details.getId());

        var tags = PlayerDao.getTags(this.details.getId());
        response.writeInt(tags.size());

        for (Tag t : tags.values()) {
            response.writeString(t.getTag());
        }


    }

    @Override
    public short getHeader() {
        return 350; // "ZWA"
    }
}
