package net.h4bbo.kepler.messages.outgoing.messenger;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.messenger.MessengerUser;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;
import net.h4bbo.kepler.util.config.GameConfiguration;

import java.util.List;

public class FRIENDLIST extends MessageComposer {
    private final Player player;
    private final List<MessengerUser> friends;

    public FRIENDLIST(Player player, List<MessengerUser> friends) {
        this.player = player;
        this.friends = friends;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.friends.size());

        for (MessengerUser friend : this.friends) {
            friend.serialise(response);
        }
    }

    @Override
    public short getHeader() {
        return 263; // "DG"
    }
}