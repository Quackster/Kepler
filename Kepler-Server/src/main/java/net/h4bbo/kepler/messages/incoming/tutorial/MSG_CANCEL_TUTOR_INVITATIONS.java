package net.h4bbo.kepler.messages.incoming.tutorial;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class MSG_CANCEL_TUTOR_INVITATIONS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (!player.getGuideManager().isGuidable()) {
            return;
        }

        if (!player.getGuideManager().isWaitingForGuide()) {
            return;
        }

        player.getGuideManager().setWaitingForGuide(false);
        player.getGuideManager().setGuidable(false);
        player.getGuideManager().getInvited().clear();
    }
}
