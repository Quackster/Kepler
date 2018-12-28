package org.alexdev.kepler.messages.outgoing.wobblesquabble;

import org.alexdev.kepler.game.games.wobblesquabble.WobbleSquabblePlayer;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class PT_PREPARE extends MessageComposer {
    private final WobbleSquabblePlayer player1;
    private final WobbleSquabblePlayer player2;

    public PT_PREPARE(WobbleSquabblePlayer player1, WobbleSquabblePlayer player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeDelimeter("0:" + this.player1.getPlayer().getRoomUser().getInstanceId(), (char)13);
        response.write("1:" + this.player2.getPlayer().getRoomUser().getInstanceId());
    }

    @Override
    public short getHeader() {
        return 115; // "As"
    }
}
