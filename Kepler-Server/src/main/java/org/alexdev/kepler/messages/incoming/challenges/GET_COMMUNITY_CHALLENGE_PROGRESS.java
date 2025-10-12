package org.alexdev.kepler.messages.incoming.challenges;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.challenges.COMMUNITY_GOAL_PROGRESS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_COMMUNITY_CHALLENGE_PROGRESS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        player.send(new COMMUNITY_GOAL_PROGRESS());
    }
}
