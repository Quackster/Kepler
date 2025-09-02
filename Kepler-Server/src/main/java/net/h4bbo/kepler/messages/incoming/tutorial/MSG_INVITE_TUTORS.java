package net.h4bbo.kepler.messages.incoming.tutorial;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.tutorial.INVITATION_SENT;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;

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
