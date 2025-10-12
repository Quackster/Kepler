package org.alexdev.kepler.messages.incoming.challenges;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.challenges.DAILY_TASKS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_DAILY_TASKS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        player.send(new DAILY_TASKS());
    }
}
