package org.alexdev.kepler.messages.outgoing.navigator;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class NODESPACEUSERS extends MessageComposer {
    private final List<Player> players;

    public NODESPACEUSERS(List<Player> players) {
        this.players = players;
    }

    @Override
    public void compose(NettyResponse response) {
        for (Player player : this.players) {
            response.writeString(player.getDetails().getName());
        }
    }

    @Override
    public short getHeader() {
        return 223; // "C_"
    }
}
