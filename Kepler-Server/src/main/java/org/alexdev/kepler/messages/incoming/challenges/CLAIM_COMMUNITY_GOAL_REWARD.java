package org.alexdev.kepler.messages.incoming.challenges;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class CLAIM_COMMUNITY_GOAL_REWARD implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        final String goalCode = reader.readString();
        final int rewardId = reader.readInt();

        // TODO: Handle.
    }
}
