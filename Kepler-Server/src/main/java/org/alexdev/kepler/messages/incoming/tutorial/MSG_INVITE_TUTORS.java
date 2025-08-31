package org.alexdev.kepler.messages.incoming.tutorial;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.tutorial.INVITATION_SENT;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.util.concurrent.TimeUnit;

public class MSG_INVITE_TUTORS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (!player.getGuideManager().isGuidable()) {
            return;
        }

        if (player.getGuideManager().isWaitingForGuide()) {
            return;
        }

        player.send(new INVITATION_SENT());

        player.getGuideManager().setStartedForWaitingGuidesTime((int) (DateUtil.getCurrentTimeSeconds() + TimeUnit.MINUTES.toSeconds(GameConfiguration.getInstance().getInteger("guide.search.timeout.minutes"))));
        player.getGuideManager().setWaitingForGuide(true);
    }
}
